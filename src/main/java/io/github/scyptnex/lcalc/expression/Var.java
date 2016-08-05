package io.github.scyptnex.lcalc.expression;

public class Var implements Term {

    private final String baseName;
    private int alphaVersion;

    public Var(String name){
        this.alphaVersion = 0;
        this.baseName = name;
    }

    @Override
    public String toDisplayString() {
        return baseName + versionToString(alphaVersion);
    }

    public static String versionToString(int v){
        switch(v){
            case 0 : return "";
            case 1 : return "'";
            case 2 : return "\"";
            default: return "" + (v+1);
        }
    }

    public String getBaseName() {
        return baseName;
    }

    public int getAlphaVersion() {
        return alphaVersion;
    }

    public void setAlphaVersion(int alphaVersion) {
        this.alphaVersion = alphaVersion;
    }
}
