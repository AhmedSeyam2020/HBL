import java.util.*;

abstract aspect GenericAspect<A,B> {

  interface SimpleI<L extends Number> {}

  declare parents: A implements SimpleI<B>; // Error: 'B' specified in the GenericAspect declaration doesnt say 'B extends Number'

  public N SimpleI<N>.m4(N n) { System.err.println(n);return n;}

}

aspect GenericAspectK extends GenericAspect<Base,String> {  // Error: String doesnt extend Number
  public static void main(String []argv) {
    Base b = new Base();
    String s = b.m4("hello");
    if (!s.equals("hello"))
      throw new RuntimeException("Not hello?? "+s);
  }
}

class Base {}

