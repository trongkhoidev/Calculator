package Calculator_Team3;

public class FormatValue {
    // Convert the value to a string with the specified format
    protected double stringRadixToDouble(String str, int radix) {
        str = str.toUpperCase();
        int len = str.length();
        int indexDot = -1;
        double num = -1;
        String radixChar = "0123456789ABCDEF";

        // If the string contains a decimal point, separate the integer and fractional parts
        if (!isRadixString(str, radix)) {
            return num;
        }
        // Find the position of the decimal point
        indexDot = str.indexOf(".");
        if (indexDot > 0) {
            // String is in the form of "integer.fraction"
            String intString = str.substring(0, indexDot);
            // String is in the form of "integer.fraction"
            String floatString = str.substring(indexDot + 1, len);
            // Convert the integer part to a double
            num = Integer.parseInt(intString, radix);
                  
            for (int i = 0; i < floatString.length(); i++) {
                num += radixChar.indexOf(floatString.charAt(i))
                        / Math.pow(radix, i + 1);
            }
        } else {
            num = Integer.parseInt(str, radix);
        }
        return num;
    }

    // Check if the string is a valid radix string
    protected String doubleToStringRadix(double num, int radix, int countRount) {
        String str = "";
        String radixChar = "0123456789ABCDEF";
        long intNum = (long) num;
        double floatNum = (num - intNum);
        // Convert the integer part to a string
        String intString = Long.toString(intNum, radix).toUpperCase();
        String floatString = "";
        // Convert the fractional part to a string
        while (floatNum > 0 && countRount > 0) {
            floatNum = (floatNum * radix);
            floatString += radixChar.charAt((int) floatNum);
            floatNum = floatNum - (int) floatNum;
            countRount--;
        }
        str = intString;
        if (floatString.length() > 0) {
            str += "." + floatString;
        }
        return str;
    }

    // Check if the string is a valid representation in the specified radix
    protected boolean isRadixString(String str, int radix) {
        str = str.toUpperCase();
        int len = str.length();
        int countDot = 0;
        String radixChar = "";
        if (radix == 2) {
            radixChar = "01.";
        } else if (radix == 8) {
            radixChar = "01234567.";
        } else if (radix == 16) {
            radixChar = "0123456789ABCDEF.";
        } else {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == '.') {
                countDot++;
            }
            if (radixChar.indexOf(c) < 0 || countDot > 1) {
                return false;
            }
        }
        return true;
    }

    // Convert a double number to its prime factorization representation
    protected String primeMulti(double numDouble) {
        if (numDouble >= 0 && (long) numDouble == numDouble) {
            long num = (long) numDouble;
            // powCount is the exponent of the factor
            int powCount = 0, i = 2, m = (int) Math.sqrt(num) + 1;
            String s = "";
            // Iterate from 2 to the square root of num
            while (i < m) {
                powCount = 0; // Exponent of i
                // Factor num into i^n
                while (num > 0 && num % i == 0) {
                    powCount++;
                    num /= i;
                }
                // Add the factor if it divides evenly
                if (powCount > 0) {
                    if (s.length() > 0) {
                        s += "×"; // Add multiplication sign
                    }
                    s += i; // Add the factor
                    if (powCount > 1) {
                        s += myPowAxB(powCount); // Add exponent
                    }
                }
                if (i == 2) {
                    i++;
                } else {
                    i += 2;
                }
            }
            // Check if num is a prime number after the loop
            if (num > 1) {
                if (s.length() > 0) {
                    s += "×";
                }
                s += num;
            } else if (s.length() == 0) {
                s += num;
            }
            return s;
        }
        return "-1";
    }

    // Convert an integer to its exponent representation
    private String myPowAxB(int number) {
        String numString = number + "";
        String numPow = "⁰¹²³⁴⁵⁶⁷⁸⁹";
        String result = "";
        for (int i = 0; i < numString.length(); i++) {
            result += numPow.charAt(Integer.parseInt(numString.charAt(i) + ""));
        }
        return result;
    }
}
