
    public static void main (String[] args) {
        class Bla implements CB {
            public void cb_sub_setup (String id, Hashtable param) {
            }
            public void cb_sub_done (String id, Hashtable param) {
            }
            public String cb_sub_exec (String id, Hashtable unused, Object privData) {
                X ("Request: " + id);
                for (Enumeration e = unused.keys (); e.hasMoreElements (); ) {
                    String  key = (String) e.nextElement ();
                    String  val = (String) unused.get (key);
                    
                    X ("Parm '" + key + "' = '" + val + "'");
                }
                return "[[[" + id + "]]]";
            }
        }
            
        Sub  s = new Sub ();
        
        s.parse ("<a href=\"[rdir-domain bla=fasel]/banner_click?bid={banner}&uid=[agnUID]\"><img src=\"[rdir-domain dflt=\"bla fasel\"]/banner?bid={banner}&uid=[agnUID]\" border=\"0\"></a>",
             "\\[([^]]+)\\]", "[ \t]*([^ \t]+)", "([A-Za-z0-9_-]+)=(\"[^\"]*\"|[^ \t]*)", "^\"(.*)\"$");
        s.reg ("rdir-domain", new Bla ());
        X (s.sub (null));
    }
    public static void X (Object s) {
        System.out.println (">> " + s.toString ());
    }
