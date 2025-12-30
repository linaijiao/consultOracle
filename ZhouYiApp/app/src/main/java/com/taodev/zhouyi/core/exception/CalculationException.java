package com.taodev.zhouyi.core.exception;

/**
 * 计算异常类
 * 用于处理各种计算相关的异常情况
 */
public class CalculationException extends RuntimeException {

    /**
     * 错误码
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 原始异常
     */
    private Throwable originalException;

    // ============ 构造方法 ============

    /**
     * 默认构造方法
     */
    public CalculationException() {
        super();
    }

    /**
     * 带错误信息的构造方法
     * @param message 错误信息
     */
    public CalculationException(String message) {
        super(message);
        this.errorMessage = message;
    }

    /**
     * 带错误码和错误信息的构造方法
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    public CalculationException(int errorCode, String errorMessage) {
        super(formatMessage(errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 带异常原因的构造方法
     * @param cause 原始异常
     */
    public CalculationException(Throwable cause) {
        super(cause);
        this.originalException = cause;
    }

    /**
     * 带错误信息和异常原因的构造方法
     * @param message 错误信息
     * @param cause 原始异常
     */
    public CalculationException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
        this.originalException = cause;
    }

    /**
     * 完整参数的构造方法
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param cause 原始异常
     */
    public CalculationException(int errorCode, String errorMessage, Throwable cause) {
        super(formatMessage(errorCode, errorMessage), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.originalException = cause;
    }

    // ============ Getter 和 Setter 方法 ============

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage != null ? errorMessage : getMessage();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Throwable getOriginalException() {
        return originalException;
    }

    public void setOriginalException(Throwable originalException) {
        this.originalException = originalException;
    }

    // ============ 工具方法 ============

    /**
     * 格式化错误信息
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 格式化后的错误信息
     */
    private static String formatMessage(int errorCode, String errorMessage) {
        return String.format("ErrorCode: %d, Message: %s", errorCode, errorMessage);
    }

    /**
     * 判断是否是特定错误码的异常
     * @param code 错误码
     * @return 是否是特定错误码
     */
    public boolean isErrorCode(int code) {
        return this.errorCode == code;
    }

    /**
     * 获取完整的异常信息（包含错误码和原始异常）
     * @return 完整的异常信息
     */
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("CalculationException: ");

        if (errorCode != 0) {
            sb.append("ErrorCode=").append(errorCode).append(", ");
        }

        if (errorMessage != null) {
            sb.append("Message=").append(errorMessage);
        } else if (getMessage() != null) {
            sb.append("Message=").append(getMessage());
        }

        if (originalException != null) {
            sb.append(", OriginalException=").append(originalException.getClass().getName());
            if (originalException.getMessage() != null) {
                sb.append(": ").append(originalException.getMessage());
            }
        }

        return sb.toString();
    }

    // ============ 常用错误码 ============

    /**
     * 计算异常错误码常量定义
     */
    public interface ErrorCodes {
        int DIVISION_BY_ZERO = 1001;      // 除零错误
        int INVALID_INPUT = 1002;         // 无效输入
        int OVERFLOW_ERROR = 1003;        // 溢出错误
        int UNDERFLOW_ERROR = 1004;       // 下溢错误
        int INVALID_OPERATION = 1005;     // 无效操作
        int TIMEOUT = 1006;               // 计算超时
        int INSUFFICIENT_DATA = 1007;     // 数据不足
        int INVALID_PARAMETER = 1008;     // 参数无效
        int NETWORK_ERROR = 1009;         // 网络错误（如果需要远程计算）
        int UNKNOWN_ERROR = 9999;         // 未知错误
    }

    /**
     * 创建除零异常
     * @return CalculationException
     */
    public static CalculationException createDivisionByZeroException() {
        return new CalculationException(
                ErrorCodes.DIVISION_BY_ZERO,
                "Division by zero is not allowed"
        );
    }

    /**
     * 创建无效输入异常
     * @param input 无效的输入
     * @return CalculationException
     */
    public static CalculationException createInvalidInputException(String input) {
        return new CalculationException(
                ErrorCodes.INVALID_INPUT,
                String.format("Invalid input: %s", input)
        );
    }

    /**
     * 创建溢出异常
     * @param operation 操作名称
     * @param value 导致溢出的值
     * @return CalculationException
     */
    public static CalculationException createOverflowException(String operation, Object value) {
        return new CalculationException(
                ErrorCodes.OVERFLOW_ERROR,
                String.format("Overflow occurred during %s with value: %s", operation, value)
        );
    }

    /**
     * 创建下溢异常
     * @param operation 操作名称
     * @param value 导致下溢的值
     * @return CalculationException
     */
    public static CalculationException createUnderflowException(String operation, Object value) {
        return new CalculationException(
                ErrorCodes.UNDERFLOW_ERROR,
                String.format("Underflow occurred during %s with value: %s", operation, value)
        );
    }

    /**
     * 快速判断是否是特定类型的异常
     */
    public boolean isDivisionByZero() {
        return isErrorCode(ErrorCodes.DIVISION_BY_ZERO);
    }

    public boolean isOverflow() {
        return isErrorCode(ErrorCodes.OVERFLOW_ERROR);
    }

    public boolean isUnderflow() {
        return isErrorCode(ErrorCodes.UNDERFLOW_ERROR);
    }

    public boolean isInvalidInput() {
        return isErrorCode(ErrorCodes.INVALID_INPUT);
    }
}
