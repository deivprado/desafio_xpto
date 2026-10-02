package davi.prado.XPTO.exception;

public class MovimentacaoJaExisteException extends RuntimeException {
    public MovimentacaoJaExisteException(String message) {
        super(message);
    }
}
