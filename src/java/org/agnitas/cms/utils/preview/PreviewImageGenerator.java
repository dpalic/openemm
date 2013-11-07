package org.agnitas.cms.utils.preview;

import javax.imageio.*;
import javax.servlet.http.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.util.concurrent.*;
import org.agnitas.cms.utils.*;
import org.agnitas.cms.utils.dataaccess.*;
import org.agnitas.cms.web.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;
import org.springframework.context.*;

/**
 * This class generate image from browser`s page.
 * Stores it image in database.
 */
public class PreviewImageGenerator {

	JEditorPane editor;
	private boolean imagesLoaded = false;
	private boolean pageLoaded = false;
	private boolean rendered = false;
	private int initialWidth = 800;
	private int initialHeight = 600;
	private ApplicationContext aContext;
	private HttpSession session;
	private int cmTemplateId;
	private int cmId;
	private int cmtId;
	private ThreadPoolExecutor threadPool;
	private int previewMaxWidth;
	private int previewMaxHeigth;

	public PreviewImageGenerator(ApplicationContext applicationContext,
								 HttpSession session, final int previewMaxWidth,
								 final int previewMaxHeight) {
		aContext = applicationContext;
		this.session = session;
		final int nThreads = 4;
		final long keepAliveTime = 30;//seconds
		final LinkedBlockingQueue<Runnable> runnables =
				new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(nThreads, nThreads, keepAliveTime,
				TimeUnit.SECONDS, runnables);
		this.previewMaxWidth = previewMaxWidth;
		previewMaxHeigth = previewMaxHeight;
	}

	/**
	 * Generate preview image for one of cms`s element id,
	 * element for wich generates preview must be non zero value and
	 * other two must be equals to zero.
	 *
	 * @param cmTemplateId id of cms`s template
	 * @param cmId		 id of content module
	 * @param cmtId		id of content module type
	 */
	public void generatePreview(int cmTemplateId, int cmId, int cmtId) {
		this.cmTemplateId = cmTemplateId;
		this.cmId = cmId;
		this.cmtId = cmtId;

		String previewUrl = generatePreviewUrl(cmTemplateId, cmId, cmtId);
		if(previewUrl == null) {
			return;
		}
		String systemUrl = AgnUtils.getEMMProperty("system.url");
		final String finalPreviewUrl = systemUrl + previewUrl;

		AgnUtils.logger().info("HTML-preview URL is " + finalPreviewUrl);

		threadPool.execute(new Thread() {
			@Override
			public void run() {
				generatePreview(finalPreviewUrl);
			}
		});
	}

	void generatePreview(String url) {
		AgnUtils.logger().info("Trying to set headless mode");
		System.setProperty("java.awt.headless", "true");
		AgnUtils.logger().info("Creating swing html editor");
		editor = new JEditorPane();
		CmsEditorKit editorKit = new CmsEditorKit() {
			@Override
			public void onImagesLoaded() {
				imagesLoaded = true;
				AgnUtils.logger().info("preview`s image load finished");
			}
		};
		editor.setEditorKit(editorKit);
		editor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if("page".equals(event.getPropertyName())) {
					onPageLoaded();
				}
			}
		});

		try {
			editor.setPage(url);
		} catch(IOException e) {
			AgnUtils.logger().error("URL for preview generation is not valid: "
					+ e + "\n" + AgnUtils.getStackTrace(e));
		}

		while(!(pageLoaded && imagesLoaded)) {
			Thread.yield();
		}

		renderPreview();
	}

	private void onPageLoaded() {
		editor.setDoubleBuffered(false);
		editor.setSize(initialWidth, initialHeight);
		editor.addNotify();
		editor.validate();
		BufferedImage dummyImage =
				new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
		Graphics2D imageGraphics = dummyImage.createGraphics();
		editor.paint(imageGraphics);
		pageLoaded = true;
		if(!imagesLoaded) {
			imagesLoaded = ((CmsEditorKit) editor.getEditorKit()).getImageCount() == 0;
		}
		AgnUtils.logger().info("page for generation preview was loaded");
	}

	private void renderPreview() {
		if(imagesLoaded && pageLoaded && !rendered) {
			AgnUtils.logger().info("preview`s image rendering started...");

			// determine rendering page size
			Dimension preferredSize = editor.getPreferredSize();
			Dimension minimumSize = editor.getMinimumSize();
			Dimension currentSize = editor.getSize();
			if(minimumSize.width > currentSize.width ||
					minimumSize.height > currentSize.height) {
				currentSize.width = minimumSize.width;
				currentSize.height = minimumSize.height;
			} else if(preferredSize.width < currentSize.width) {
				currentSize.width = preferredSize.width;
				currentSize.height = preferredSize.height;
			} else if(preferredSize.height < currentSize.height) {
				currentSize.height = preferredSize.height;
			}

			// relayout page as we have now the new size and all images loaded
			editor.setSize(currentSize.width, currentSize.height);
			editor.addNotify();
			editor.validate();

			// paint page on image
			BufferedImage originalImage = new BufferedImage(currentSize.width,
					currentSize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D imageGraphics = originalImage.createGraphics();
			editor.paint(imageGraphics);

			// scale image
			Dimension previewSize =
					getPreviewSize(currentSize.width, currentSize.height);
			BufferedImage resultImage;
			if(previewSize.width > currentSize.width) {
				resultImage = originalImage;
			} else {
				Image scaledImage = originalImage
						.getScaledInstance(previewSize.width,
								previewSize.height,
								Image.SCALE_SMOOTH);
				resultImage =
						new BufferedImage(previewSize.width, previewSize.height,
								BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = resultImage.createGraphics();
				graphics.drawImage(scaledImage, 0, 0, null);
				graphics.dispose();
			}

			try {
				ByteArrayOutputStream byteArrayOutputStream =
						new ByteArrayOutputStream();
				ImageIO.write(resultImage, "png", byteArrayOutputStream);
				byte[] imageData = byteArrayOutputStream.toByteArray();
				storePreview(imageData);
			} catch(IOException e) {
				AgnUtils.logger()
						.error("Error occured while saving preview-image: "
								+ e + "\n" + AgnUtils.getStackTrace(e));
			}
			AgnUtils.logger().info("preview`s image rendering finished");
			rendered = true;
		}

	}

	private void storePreview(byte[] imageData) {
		MediaFileManager mediaFileManager =
				CmsUtils.getMediaFileManager(aContext);
		MediaFile mediaFile =
				new MediaFile(cmTemplateId, 1, imageData, cmId, cmtId, 0,
						MediaFileUtils.PREVIEW_TYPE, "image/png", "preview");
		if(cmId != 0) {
			mediaFileManager.removePreviewOfContentModule(cmId);
		} else if(cmtId != 0) {
			mediaFileManager.removePreviewOfContentModuleType(cmtId);
		} else if(cmTemplateId != 0) {
			mediaFileManager.removePreviewOfContentModuleTemplate(cmTemplateId);
		}
		mediaFileManager.createMediaFile(mediaFile);
	}

	private Dimension getPreviewSize(int originalWidth, int originalHeight) {
		double scaleX = ((double) previewMaxWidth) / ((double) originalWidth);
		double scaleY = ((double) previewMaxHeigth) / ((double) originalHeight);
		double scale = Math.min(scaleX, scaleY);
		return new Dimension((int) (scale * originalWidth),
				(int) (scale * originalHeight));
	}

	private String generatePreviewUrl(int cmTemplateId, int cmId, int cmtId) {
		String sessionId = session.getId();
		if(cmId > 0) {
			return "/cms_contentmodule.do;jsessionid=" + sessionId +
					"?action=" +
					ContentModuleAction.ACTION_PURE_PREVIEW +
					"&contentModuleId=" + cmId;
		} else if(cmTemplateId > 0) {
			return "/cms_cmtemplate.do;jsessionid=" + sessionId + "?action=" +
					CMTemplateAction.ACTION_PURE_PREVIEW + "&cmTemplateId=" +
					cmTemplateId;
		} else if(cmtId > 0) {
			return "/cms_cmt.do;jsessionid=" + sessionId + "?action=" +
					ContentModuleTypeAction.ACTION_PURE_PREVIEW + "&cmtId=" +
					cmtId;
		} else {
			return null;
		}
	}

}
