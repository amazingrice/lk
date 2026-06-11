public class Token {
	public int type;

	public String getTypeName() {
        switch (type) {
            case TokenTypes.KLASSE:       return "KLASSE";
            case TokenTypes.TYP:          return "TYP";
            case TokenTypes.WENN:         return "WENN";
            case TokenTypes.SONST:        return "SONST";
            
            case TokenTypes.BLOCKAUF:     return "BLOCKAUF";
            case TokenTypes.BLOCKZU:      return "BLOCKZU";
            case TokenTypes.SEMIKOLON:    return "SEMIKOLON";
            case TokenTypes.ZUWEISUNGSOP: return "ZUWEISUNGSOP";
            
            case TokenTypes.ZAHL:         return "ZAHL";
            case TokenTypes.NAME:         return "NAME";
            
            default:                      return "UNKNOWNTYPE";
        }
    }
}
