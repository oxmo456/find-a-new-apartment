package eu.seria

package object utils {


  def noop[A, B]: PartialFunction[A, B] = new PartialFunction[A, B] {

    override def isDefinedAt(x: A): Boolean = false

    override def apply(v1: A): B = throw new IllegalStateException()
  }


}
