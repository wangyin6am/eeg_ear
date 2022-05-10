package com.example.testversion.fragment5;

public class Complex implements Cloneable{
    private double a,b;

    public Complex() {
        this(0);
    }

    public Complex(double a) {
        this.a = a;
    }

    public Complex(double a, double b) {
        this.a = a;
        this.b = b;
    }

    //返回复数的加法运算
    public Complex add(Complex c) {
        if(c == null)    //如果c是null，抛出异常
            throw new NullPointerException("Invalid Pointer");

        return new Complex((a + c.a), (b + c.b));
    }

    //返回复数的减法运算
    public Complex substract(Complex c) {
        if(c == null)
            throw new NullPointerException("Invalid Pointer");

        return new Complex((a - c.a), (b - c.b));
    }

    //返回复数的乘法运算
    public Complex multiply(Complex c) {
        if(c == null)
            throw new NullPointerException("Invalid Pointer");

        return new Complex((a * c.a - b * c.b),(a * c.b + b * c.a));
    }

    //返回复数的除法运算
    public Complex divide(Complex c) {
        if(c == null)
            throw new NullPointerException("Invalid Pointer");

        return new Complex((a * c.a + b * c.b) / (Math.pow(c.a, 2) + Math.pow(c.b, 2)),
                (b * c.a - a * c.b) / (Math.pow(c.a, 2) + Math.pow(c.b, 2)));
    }

    //返回复数的绝对值
    public double abs() {
        return Math.sqrt(a * a + b *b);
    }

    @Override
    public String toString() {
        if(Math.abs(b) < 0.00001)
            //如果b为0，返回a(考虑到浮点数的精度变化，故用Math.abs()方法)
            return ""+a;
        else
            return a+" + "+b+"i";
    }

    @Override    //重写clone()方法
    public Object clone(){
        try {
            return super.clone();    //调用本地方法返回克隆

        }catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //返回实部
    public double getRealPart() {
        return a;
    }

    //返回虚部
    public double getImginaryPart() {
        return b;
    }

    //设置实部
    public void setRealPart(double a) {
        this.a = a;
    }

    //设置虚部
    public void setImginaryPart(double b) {
        this.b = b;
    }
}
