package org.jetbrains.spek.engine

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.engine.support.AbstractSpekTestEngineTest
import org.junit.gen5.api.Test

/**
 * @author Ranie Jade Ramiso
 */
class SubjectTest: AbstractSpekTestEngineTest() {
    class Foo {
        fun bar() { }
    }
    class SubjectSpec: Spek({
        group(Foo::class, "given: a Foo") {
            subject { Foo() }
            test("test #1") {
                subject1 = subject
            }

            test("test #2") {
                subject2 = subject
            }
        }
    }) {
        companion object {
            lateinit var subject1: Foo
            lateinit var subject2: Foo
        }
    }

    @Test
    fun testDifferentInstancePerTest() {
        executeTestsForClass(SubjectSpec::class)
        assertThat(SubjectSpec.subject1, !sameInstance(SubjectSpec.subject2))
    }

    @Test
    fun testNotConfiguredSubject() {
        class SubjectSpec: Spek({
            group(Foo::class, "given a Foo") {
                test("this should fail") {
                    subject.bar()
                }
            }
        })

        val recorder = executeTestsForClass(SubjectSpec::class)

        assertThat(recorder.testFailureCount, equalTo(1))
        val throwable = recorder.getFailingTestEvents().first().result.throwable
        fun isSpekException(throwable: Throwable) = throwable is SpekException
        assertThat(throwable.get(), ::isSpekException)
    }
}