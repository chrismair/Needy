package gdep

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage=false, includeNames=true)
class Dependency {
	
	String group
	String name
	String version
	String configuration
	
}