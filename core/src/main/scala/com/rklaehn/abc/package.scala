package com.rklaehn

import algebra.Eq

import scala.reflect.ClassTag
import scala.util.hashing.Hashing

package object abc {

  implicit class ClassTagCompanionOps(private val c: ClassTag.type) extends AnyVal {

    def apply[T](implicit ev: ClassTag[T]): ClassTag[T] = ev
  }

  implicit class ClassTagOps[T](private val underlying: ClassTag[T]) extends AnyVal {

    def singletonArray(value: T): Array[T] = {
      val a = underlying.newArray(1)
      a(0) = value
      a
    }

    def emptyArray: Array[T] = underlying.newArray(0)
  }

  implicit class HashingOps(private val underlying: Hashing.type) extends AnyVal {
    def apply[T](implicit ev: Hashing[T]): Hashing[T] = ev
    def by[@specialized A, @specialized B](f: A ⇒ B)(implicit bh: Hashing[B]): Hashing[A] = new Hashing[A] {
      override def hash(x: A): Int = bh.hash(f(x))
    }
  }

  // $COVERAGE-OFF$
  implicit class ArrayOps[T](private val underlying: Array[T]) extends AnyVal {

    def updated(index: Int, value: T): Array[T] = {
      val result = underlying.clone
      result(index) = value
      result
    }

    def patched(index: Int, value: T)(implicit c: ClassTag[T]): Array[T] = {
      val result = new Array[T](underlying.length + 1)
      System.arraycopy(underlying, 0, result, 0, index)
      result(index) = value
      if (index < underlying.length)
        System.arraycopy(underlying, index, result, index + 1, underlying.length - index)
      result
    }

    def resizeInPlace(n: Int)(implicit c: ClassTag[T]): Array[T] =
      if (underlying.length == n)
        underlying
      else {
        val r = c.newArray(n)
        System.arraycopy(underlying, 0, r, 0, n min underlying.length)
        r
      }
  }

  // todo: remove once algebra has array instances (or use spire for instances once that moves to algebra)?
  implicit def arrayEq[A](implicit aEq: Eq[A]): Eq[Array[A]] = new Eq[Array[A]] {
    def eqv(x: Array[A], y: Array[A]): Boolean = {
      x.length == y.length && {
        var i = 0
        while(i < x.length) {
          if(!aEq.eqv(x(i), y(i)))
            return false
          i += 1
        }
        true
      }
    }
  }
  // $COVERAGE-ON$
}
