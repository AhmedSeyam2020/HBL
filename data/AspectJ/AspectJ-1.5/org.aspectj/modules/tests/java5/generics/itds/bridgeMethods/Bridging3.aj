// this bridge stuff is handled by the compiler
// Similar to previous type var test but now the String parameter should
// be Object in the bridge method
import java.lang.reflect.*;

abstract class C<A> {
	abstract A id(A x);
}

class D extends C<String> {
	String id(String s) {return s;}
}

public aspect Bridging3 {
	public static void main(String []argv) {
		Util.dumpMethods("D");
	}
}
