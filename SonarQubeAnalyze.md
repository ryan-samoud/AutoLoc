# I)Impact Faible
* Remove the 'ApiResponses' wrapper from this annotation group [java:S1710]
* Remove this unused import 'java.util.ArrayList'. [java:S1128]
* Reorder the modifiers to comply with the Java Language Specification. [java:S1124]
* Rename this generic name to match the regular expression '^[A-Z][0-9]?$'. [java:S119]
# II)Impact Moyen
* Remove this unused method parameter "repository". [java:S1172]
* Consume or remove this unused collection [java:S4030]
* ID is not used in the interface. [java:S2326]
* Rename this bean to match the regular expression '^[a-z][a-zA-Z0-9]*$'. [java:S6830]
* Replace this use of System.out by a logger. [java:S106]
* Add a private constructor to hide the implicit public one. [java:S1118]
# III)Impact Important
* Replace this persistent entity with a simple POJO or DTO object. [java:S4684]
* Add at least one assertion to this test case. [java:S2699]