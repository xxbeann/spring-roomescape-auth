package roomescape.exception;

public class WrongMarketAccessException extends BusinessException {

    public WrongMarketAccessException() {
        super(ErrorType.WRONG_MARKET_ACCESS);
    }
}
