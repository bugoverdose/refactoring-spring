package study.refactoringspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RefactoringSpringApplication

fun main(args: Array<String>) {
    runApplication<RefactoringSpringApplication>(*args)
}
