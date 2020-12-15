import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * In this testcase we are using cflow() with a pointcut that doesn't include state -
 * this should be managed by our new CflowCounter rather than a CflowStack.
 * 
 * Because the two cflow pointcuts are identical (both are 'cflow(execution(* main(..))' it also
 * means we can share a single counter for both of them !
 */
public class CounterTest01 {

	public static void main(String []argv) {
		new CounterTest01().sayMessage();
		int ctrs = ReflectionHelper.howManyCflowCounterFields(Cflow1.aspectOf());
		if (ctrs!=1) {
			throw new RuntimeException("Should be one cflow counter, but found: "+ctrs);
		}
		int stacks = ReflectionHelper.howManyCflowStackFields(Cflow1.aspectOf());
		if (stacks!=1) {
			throw new RuntimeException("Should be one cflow stacks, but found: "+stacks);
		}
	}
	
	public void sayMessage() {
		print("Hello ");
		print("World\n");
	}
	
	public void print(String msg) {
		System.out.print(msg);
	}
}

aspect Cflow1 {
	before(): execution(* print(..)) && cflow(execution(* main(..))) {
		// Managed by a CflowCounter
	}
	
	before(): execution(* print(..)) && cflow(execution(* main(..))) {
		// Managed by a CflowCounter
	}
	
	before(Object o): execution(* print(..)) && cflow(execution(* main(..)) && target(o)) {
		// Managed by a CflowStack - since state is exposed
	}
}

class ReflectionHelper {
  public static List getCflowfields(Object o,boolean includeCounters,boolean includeStacks) {
  	List res = new ArrayList();
  	Class clazz = o.getClass();
  	Field[] fs = clazz.getDeclaredFields();
  	for (int i = 0; i < fs.length; i++) {
		Field f = fs[i];
		if ((f.getType().getName().endsWith("CFlowCounter") && includeCounters) ||
			(f.getType().getName().endsWith("CFlowStack") && includeStacks)) {
			res.add(f.getType().getName()+":"+f.getName());
		}
	}
  	return res;
  }
  
  public static int howManyCflowCounterFields(Object o) {
    return getCflowfields(o,true,false).size();
  }
  
  public static int howManyCflowStackFields(Object o) {
    return getCflowfields(o,false,true).size();
  }
  
}
