package com.rklaehn.abc

abstract class NoEquals {
  final override def hashCode(): Int =
    throw new UnsupportedOperationException("Use Hashing[T] typeclass")
  final override def equals(that: Any): Boolean =
    throw new UnsupportedOperationException("Use Eq[T] typeclass")
  override def toString: String =
    getClass.getName + "@" + System.identityHashCode(this)
}
