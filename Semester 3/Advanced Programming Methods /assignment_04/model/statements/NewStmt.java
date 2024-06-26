package model.statements;

import model.PrgState;
import model.exceptions.MyExc;
import model.expressions.Exp;
import model.types.RefType;
import model.values.RefValue;
import model.values.Value;
import java.util.Objects;

public class NewStmt implements IStmt{
    private String varName;
    private Exp expression;
    public NewStmt(String variable_name, Exp exp)
    {
        this.varName = variable_name;
        this.expression = exp;
    }
    @Override
    public PrgState execute(PrgState state) throws MyExc {
        if(!state.getSymTable().isDefined(varName))
            throw new MyExc("Variable not defined");
        Value varValue = state.getSymTable().lookUp(varName);

        if(!(varValue.getType() instanceof RefType))
            throw new MyExc("Variable is not an reference type");


        Value val =expression.eval(state.getSymTable(), state.getHeap());
        // Compares the inner type of the variable's type with the type of the value obtained from evaluating the expression (val).
        if(!Objects.equals(((RefType) state.getSymTable().lookUp(varName).getType()).getInner(), val.getType()))
            throw new MyExc("Variable type and expression type do not match(new)");

        int address = state.getHeap().getFree();
        state.getHeap().add(address, val);
        state.getSymTable().add(varName, new RefValue(address, val.getType()));
        return state;
    }

    @Override
    public IStmt deepCopy() {
        return new NewStmt(varName, expression);
    }

    @Override
    public String toString() {
        return "new(" +
                "varName='" + varName + '\'' +
                ", expression='" + expression.toString() + '\'' +
                ')';
    }
}
