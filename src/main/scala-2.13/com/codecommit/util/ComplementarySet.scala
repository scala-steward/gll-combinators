/*
 * Copyright (c) 2021, Daniel Spiewak
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.codecommit.util

import scala.collection.immutable.SetOps

class ComplementarySet[A](private val without: Set[A]) extends Set[A] with SetOps[A, Set, Set[A]] {
  override val size = Int.MaxValue     // should be infinite

  def this() = this(Set())

  override def empty = new ComplementarySet[A](Set())

  def contains(e: A) = !without.contains(e)

  def iterator = throw new AssertionError("Cannot iterate over a set complement")

  override def exists(f: A=>Boolean) = !without.exists(f)

  override def forall(f: A=>Boolean) = false

  def &(that: Set[A]): ComplementarySet[A] = new ComplementarySet(that -- without)

  def incl(e: A) = {
    if (without contains e)
      new ComplementarySet(without - e)
    else
      this
  }

  def excl(e: A) = new ComplementarySet(without + e)

  override def intersect(that: scala.collection.Set[A]) = that match {
    case s: ComplementarySet[A] => new ComplementarySet(without ++ s.without)
    case s: Set[A] => new ComplementarySet(new ComplementarySet(without -- s))
    case _ => new ComplementarySet(new ComplementarySet(Set(that.toList: _*)))
  }

  override def concat(other: scala.collection.IterableOnce[A]) = other match {
    case that: ComplementarySet[A] => new ComplementarySet(this.without & that.without)
    case _ => new ComplementarySet(without -- other)
  }

  override def removedAll(other: scala.collection.IterableOnce[A]) = other match {
    case that: ComplementarySet[A] => new ComplementarySet(this.without ++ that.without)
    case _ => new ComplementarySet(without ++ other)
  }

  override def map[B](f: A => B) =
    new ComplementarySet(without map f)

  override def flatMap[B](f: A => scala.collection.IterableOnce[B]) =
    new ComplementarySet(without flatMap f)

  override def subsetOf(other: scala.collection.Set[A]) = other match {
    case that: ComplementarySet[A] => that.without subsetOf this.without
    case _ => false
  }

  def empty[E] = Set[E]()

  override def toString = "ComplementarySet(%s)".format(without)

  override def equals(other: Any) = other match {
    case that: ComplementarySet[A] => this.without == that.without
    case _ => false
  }

  override def hashCode = ~(without.hashCode)
}

case object UniversalCharSet extends ComplementarySet[Char]

case object UniversalOptCharSet extends ComplementarySet[Option[Char]]
