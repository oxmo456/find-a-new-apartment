package eu.seria.utils

case class Counter(value: Int) {

  def increase: Counter = Counter(value + 1)

  def decrease: Counter = Counter(value - 1)

  def ==(value: Int): Boolean = this.value == value

  def >(value: Int): Boolean = this.value > value

  def <(value: Int): Boolean = this.value < value

}
