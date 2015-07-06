import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.gui.STViewFrame;
import org.stringtemplate.v4.gui.STViz;

import java.util.HashMap;
import java.util.Map;

public class TestUsersStringTemplate {
	public static void main(String[] args) {

        STGroup.trackCreationEvents = true;
        STGroup.trackCreationEvents = true;
        STGroupDir templates = new STGroupDir(".");
		ST st = templates.getInstanceOf("page");

		st.add("users", new MyUser(143, "parrt"));
		st.add("users", new MyUser(3, "tombu"));
		st.add("users", new MyUser(99, "kg9s"));

		Map<String,String> phones = new HashMap<String, String>();
		phones.put("parrt", "x5707");
		phones.put("tombu", "x5302");
		st.add("phones", phones);

        /* Uncomment to view graphically*/
        STViewFrame viz = new STViewFrame();
        viz.setVisible(true);
        st.inspect();
		System.out.println(st.render());



	}
}

