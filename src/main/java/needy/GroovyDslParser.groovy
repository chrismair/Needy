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
		if (args.length == 0) {
			return
		}
		if (args[0] instanceof Map) {
			for (Map m: args) {
				dependencies << new Dependency([group:m.group, name:m.name, version:m.version, configuration:name])
			}
		}
		else if (args[0] instanceof String) {
//			for (String s: args) {
				String s = args[0]
				LOG.info "methodMissing: name=$name value=${s}"
				assert s.size() > 0, "String format dependency error - empty string"
				def strings = s.tokenize(':')

				def group = (strings.size() > 2) ? strings[0] : null
				def artifactName = (strings.size() < 3) ? strings[0] : strings[1]
				def version = (strings.size() == 2) ? strings[1] : strings[2]
				version = scrubVersion(version)
			
				dependencies << new Dependency([group:group, name:artifactName, version:version, configuration:name])
//			}
		}
	}

	def propertyMissing(String name) {
		println "propertyMissing: $name" 
		return [:] 
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
	
	private String scrubVersion(String version) {
		if (version?.contains('@')) {
			return version.substring(0, version.indexOf('@'))
		}
		return version
	}
	
}
