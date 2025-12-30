// 1. 基本使用
try {
    // 一些计算逻辑
    if (divisor == 0) {
        throw CalculationException.createDivisionByZeroException();
    }
    result = dividend / divisor;
} catch (CalculationException e) {
    Log.e("Calculation", e.getFullMessage());
    if (e.isDivisionByZero()) {
        Toast.makeText(context, "除数不能为零", Toast.LENGTH_SHORT).show();
    }
}

// 2. 使用错误码
try {
    // 一些计算逻辑
    if (input == null || input.isEmpty()) {
        throw new CalculationException(
            CalculationException.ErrorCodes.INVALID_INPUT,
            "输入不能为空"
        );
    }
} catch (CalculationException e) {
    switch (e.getErrorCode()) {
        case CalculationException.ErrorCodes.INVALID_INPUT:
            // 处理无效输入
            break;
        case CalculationException.ErrorCodes.OVERFLOW_ERROR:
            // 处理溢出错误
            break;
    }
}

// 3. 链式异常
try {
    // 一些计算逻辑
} catch (ArithmeticException e) {
    throw new CalculationException("计算过程中出现算术错误", e);
}

// 4. 获取详细信息
catch (CalculationException e) {
    String errorInfo = String.format(
        "错误码: %d, 错误信息: %s, 原始异常: %s",
        e.getErrorCode(),
        e.getErrorMessage(),
        e.getOriginalException() != null ? 
            e.getOriginalException().getMessage() : "无"
    );
}