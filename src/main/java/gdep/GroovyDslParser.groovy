package gdep

import org.apache.log4j.Logger
import org.codehaus.groovy.control.MultipleCompilationErrorsException

class GroovyDslParser {

	private static final Logger LOG = Logger.getLogger(GroovyDslParser)

	private List<Dependency> dependencies = []

	List<Dependency> parse(String source) {
		if (source == null) {
			throw new IllegalArgumentException("Parameter closure was null")
		}

		GroovyShell shell = createGroovyShell()
		def reader = new StringReader(source)
		try {
			shell.evaluate(reader)
		} catch (MultipleCompilationErrorsException compileError) {
			LOG.error("An error occurred compiling: [$source]", compileError)
			throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
		}

		LOG.info "dependencies = $dependencies"
		return dependencies
	}

	def methodMissing(String name, args) {
		println "methodMissing: name=$name; args=$args"
		if (args[0] instanceof Map) {
			dependencies << new Dependency(args[0] + [configuration:name])
		}
	}

	private GroovyShell createGroovyShell() {
		def callDependencies = { Closure closure ->
			closure.delegate = this
			closure()
		}
		Binding binding = new Binding(dependencies:callDependencies)

		return new GroovyShell(this.class.classLoader, binding)
	}
}
