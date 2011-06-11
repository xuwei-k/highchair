package highchair.datastore.meta

trait PropertyImplicits {
  /* Set of implicits yielding properties for our mapped primitives. */
  implicit object boolProp      extends BooleanProp
  implicit object intProp       extends IntProp
  implicit object longProp      extends LongProp
  implicit object floatProp     extends FloatProp
  implicit object doubleProp    extends DoubleProp
  implicit object stringProp    extends StringProp
  implicit object dateProp      extends DateProp
  implicit object dateTimeProp  extends DateTimeProp
  implicit object keyProp       extends KeyProp
  implicit object blobKeyProp   extends BlobKeyProp
  implicit object textProp      extends TextProp

  implicit def type2option[A](implicit prop: Prop[A]): OptionalProp[A] =
    new OptionalProp(prop)
    
  implicit def type2list[A](implicit prop: Prop[A]): ListProp[A] =
    new ListProp(prop)
}
