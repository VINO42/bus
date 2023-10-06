package io.github.vino42;

/**
 * =====================================================================================
 *
 * @Created :   2023/10/5 17:46
 * @Compiler :  jdk 17
 * @Author :    VINO
 * @Email : VINO
 * @Copyright : VINO
 * @Decription :
 * =====================================================================================
 */
public interface Strategy {

    void loadPassenger(Station currentStation,Bus bus) throws InterruptedException;

    void offPassenger(Station currentStation,Bus bus) throws InterruptedException;

    void busBroken(Station currentStation,Bus bus);

    boolean onFinalStation(Station curretStation,Bus bus);
}
