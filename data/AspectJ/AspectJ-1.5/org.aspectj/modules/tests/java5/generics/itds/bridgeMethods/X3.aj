public aspect X3 {

  public Object Super3.m() {return null;}

  public static void main(String []argv) {
    Super3 s = new Sub3();
    Integer i = (Integer)s.m();
    if (i!=42) throw new RuntimeException("Should be 42 but is "+i);
  }
}
