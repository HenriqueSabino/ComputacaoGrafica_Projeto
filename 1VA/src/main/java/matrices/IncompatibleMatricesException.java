// This class was based from
// another project: https://github.com/HenriqueSabino/neural-network-library/blob/master/src/main/java/io/github/henriquesabino/math/exception/IncompatibleMatricesException.java
package main.java.matrices;

public class IncompatibleMatricesException extends RuntimeException {

    private static final long serialVersionUID = -5651429758358859592L;

    public IncompatibleMatricesException(String message) {
        super(message);
    }
}