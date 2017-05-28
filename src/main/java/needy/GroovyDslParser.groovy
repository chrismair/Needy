package needy

import java.util.Map

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
		try {
			shell.evaluate(source)
		} 
		catch (MultipleCompilationErrorsException compileError) {
			LOG.error("An error occurred compiling: [$source]", compileError)
			throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
		}

		LOG.info "dependencies = $dependencies"
		return dependencies
	}

	def methodMissing(String name, args) {
		if (args[0] instanceof Map) {
			dependencies << new Dependency(args[0] + [configuration:name])
		}
		else if (args[0] instanceof String) {
			println "methodMissing: name=$name value=${args[0]}"
			assert args[0].size() > 0, "String format dependency error - empty string"
			def strings = args[0].tokenize(':')
			println "strings=$strings"

			def group = (strings.size() > 2) ? strings[0] : null
			def artifactName = (strings.size() < 3) ? strings[0] : strings[1]
			def version = (strings.size() == 2) ? strings[1] : strings[2]
			
			dependencies << new Dependency([group:group, name:artifactName, version:version, configuration:name])
		}
	}

	private GroovyShell createGroovyShell() {
		def callDependencies = { Closure closure ->
			closure.setResolveStrategy(Closure.DELEGATE_ONLY)
			closure.delegate = this
			closure()
		}
		def doNothing = { arg1 = null, arg2 = null, arg3 = null ->	/* accept any args */ } 
		Map bindingMap = [dependencies:callDependencies].withDefault { name -> 
			return doNothing 
		}
		Binding binding = new Binding(bindingMap)

		return new GroovyShell(this.class.classLoader, binding)
	}
}

