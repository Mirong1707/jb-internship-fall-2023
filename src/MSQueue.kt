import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val initNode = Node<E>(null)
        head = AtomicReference(initNode)
        tail = AtomicReference(initNode)
    }


    override fun enqueue(element: E) {
        val newNode = Node(element)
        while (true) {
            val curTail = tail.get()
            val curTailNext = curTail.next.get()
            if (curTail === tail.get()) {
                if (curTailNext == null) {
                    if (curTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(curTail, newNode)
                        return
                    }
                } else {
                    tail.compareAndSet(curTail, curTailNext)
                }
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val curHead = head.get()
            val curTail = tail.get()
            val curNode = curHead.next.get()
            if (curHead == head.get()) {
                if (curHead == curTail) {
                    if (curNode == null) {
                        return null
                    }
                    tail.compareAndSet(curTail, curNode)
                } else {
                    val element = curNode?.element
                    if (head.compareAndSet(curHead, curNode)) {
                        head.get().element = null
                        return element
                    }
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
