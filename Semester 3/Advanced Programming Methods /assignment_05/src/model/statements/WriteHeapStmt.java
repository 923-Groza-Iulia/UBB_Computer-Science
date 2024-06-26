package model.statements;

import model.PrgState;
import model.expressions.Exp;
import model.exceptions.MyExc;
import model.types.RefType;
import model.values.RefValue;
import model.values.Value;

import java.util.Objects;

public class WriteHeapStmt implements IStmt {
    private String varName;
    private Exp expression;

    public WriteHeapStmt(String varName, Exp exp) {
        this.varName = varName;
        this.expression = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyExc {
        if (!state.getSymTable().isDefined(varName))
            throw new MyExc("Variable not defined");

        if (!(state.getSymTable().lookUp(varName) instanceof RefValue))
            throw new MyExc("Variable is not an reference type");

        Value ref = state.getSymTable().lookUp(varName);

        if (!state.getHeap().isDefined((int) ref.getVal()))
            throw new MyExc("Address is not defined in the heap");

        Value val = expression.eval(state.getSymTable(), state.getHeap());

        if (!Objects.equals(((RefType)ref.getType()).getInner().toString(), val.getType().toString()))
            throw new MyExc("Variable type and expression type do not match(write)");

        state.getHeap().update((int) ref.getVal(), val);
        return null;
    }

    @Override
    public IStmt deepCopy() { return new WriteHeapStmt(varName, expression); }

    @Override
    public String toString() {
        return "writeHeap(" +
                "varName='" + varName + '\'' +
                ", expression=" + expression.toString() +
                ')'; }
}