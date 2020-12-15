import java.util.*;

class Base { }

public class GenericMethodITD3 {

  public static void main(String[] argv) {
    List<A> as1 = new ArrayList<A>();
    List<A> as2 = new ArrayList<A>();
    new Base().simple(as1,as2); // ok, both List<A>
  }

}

class A {}
class B extends A {}

aspect X {
  <E> void Base.simple(List<E> one,List<E> two) {}
}
