package mai.student.tokenizers.python3.preprocessing;

import mai.student.intermediateStates.IStructure;
import mai.student.intermediateStates.java.DefinedClass;
import mai.student.intermediateStates.java.DefinedFunction;
import mai.student.intermediateStates.java.FileRepresentative;
import mai.student.intermediateStates.java.VariableOrConst;
import mai.student.intermediateStates.python.*;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFor;
import mai.student.tokenizers.python3.ast.nodes.async.PyAsyncFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyArg;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyArguments;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyClassDef;
import mai.student.tokenizers.python3.ast.nodes.definitions.PyFunctionDef;
import mai.student.tokenizers.python3.ast.nodes.imports.PyImport;
import mai.student.tokenizers.python3.ast.nodes.imports.PyImportFrom;
import mai.student.tokenizers.python3.ast.nodes.roots.PyExpressionContainer;
import mai.student.tokenizers.python3.ast.nodes.roots.PyInteractive;
import mai.student.tokenizers.python3.ast.nodes.roots.PyModule;
import mai.student.tokenizers.python3.ast.nodes.statements.PyAnnAssign;
import mai.student.tokenizers.python3.ast.nodes.statements.PyAssign;
import mai.student.tokenizers.python3.ast.nodes.statements.PyAugAssign;
import mai.student.tokenizers.python3.ast.visitors.AbstractPyGenericListVisitor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PyAnalysisVisitor extends AbstractPyGenericListVisitor<IStructure<PyFileRepresentative>, IStructure<PyFileRepresentative>> {

    // Заполнение файлов
    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyModule element, IStructure<PyFileRepresentative> arg) {
        element.getBody().forEach(s -> processChildren(arg, s.accept(this, arg)));

        return List.of(arg);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyInteractive element, IStructure<PyFileRepresentative> arg) {
        element.getBody().forEach(s -> processChildren(arg, s.accept(this, arg)));

        return List.of(arg);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyExpressionContainer element, IStructure<PyFileRepresentative> arg) {
        processChildren(arg, element.getBody().accept(this, arg));
        return List.of(arg);
    }

    // Заполнение классов
    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyClassDef element, IStructure<PyFileRepresentative> arg) {

        PyClassRepresentation result = new PyClassRepresentation(element.getName());
        result.setParent(arg);

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.getDecorators().add(
                    new PyDecoratorConstructorVisitor(s).getResult())
            );
        }

        if (element.getBases() != null) {
            element.getBases().forEach(s -> result.getInheritanceList().add(
                    new PyClassBaseConstructorVisitor(s).getResult()
            ));
        }


        element.getBody().forEach(s -> processChildren(result, s.accept(this, result)));

        if (element.getTypeParams() != null && !element.getTypeParams().isEmpty()) {
            result.setParametrized(true);
            element.getTypeParams().forEach(s -> result.getParams().add(
                    new PyTypeParameterConstructorVisitor(s).getResult()
            ));
        }

        return List.of(result);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyFunctionDef element, IStructure<PyFileRepresentative> arg) {

        PyFuncRepresentative result = new PyFuncRepresentative(element.getName());
        result.setSelfNode(element);
        result.setParent(arg);
        System.out.println(element.getName() + " " + arg.getName());

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.getDecorators().add(
                    new PyDecoratorConstructorVisitor(s).getResult())
            );
        }

        if (element.getArgs() != null) {
            element.getArgs().accept(this, result)
                    .forEach(v -> {
                        result.getArgTypes().add(((PyVariableRepresentative) v).getType());
                        result.getArgNames().add(v.getName());
                    });
        }

        if (element.getBody() != null) {
            element.getBody().forEach(s -> processChildren(result, s.accept(this, result)));
        }

        if (element.getReturns() != null) {
            result.setReturnValue(new PyTypeParameterConstructorVisitor(element.getReturns()).getResult());
        }

        if (element.getTypeParams() != null && !element.getTypeParams().isEmpty()) {
            result.setParametrized(true);
            element.getTypeParams().forEach(s -> result.getParams().add(
                    new PyTypeParameterConstructorVisitor(s).getResult()
            ));
        }

        return List.of(result);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyAsyncFunctionDef element, IStructure<PyFileRepresentative> arg) {

        PyFuncRepresentative result = new PyFuncRepresentative(element.getName());
        result.setAsync(true);
        result.setSelfNode(element);
        result.setParent(arg);

        if (element.getDecoratorList() != null) {
            element.getDecoratorList().forEach(s -> result.getDecorators().add(
                    new PyDecoratorConstructorVisitor(s).getResult())
            );
        }

        if (element.getArgs() != null) {
            element.getArgs().accept(this, result)
                    .forEach(v -> result.getArgTypes().add(((PyVariableRepresentative) v).getType()));
        }

        if (element.getBody() != null) {
            element.getBody().forEach(s -> processChildren(result, s.accept(this, result)));
        }

        if (element.getReturns() != null) {
            result.setReturnValue(new PyTypeParameterConstructorVisitor(element.getReturns()).getResult());
        }

        if (element.getTypeParams() != null && !element.getTypeParams().isEmpty()) {
            result.setParametrized(true);
            element.getTypeParams().forEach(s -> result.getParams().add(
                    new PyTypeParameterConstructorVisitor(s).getResult()
            ));
        }

        return List.of(result);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyArguments element, IStructure<PyFileRepresentative> arg) {
        List<IStructure<PyFileRepresentative>> result = new ArrayList<>();

        if (element.getPosonlyargs() != null) {
            element.getPosonlyargs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getArgs() != null) {
            element.getArgs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getVararg() != null) {
            result.addAll(element.getVararg().accept(this, arg));
        }
        if (element.getKwonlyargs() != null) {
            element.getKwonlyargs().forEach(s -> result.addAll(s.accept(this, arg)));
        }
        if (element.getKwarg() != null) {
            result.addAll(element.getKwarg().accept(this, arg));
        }

        processChildren(arg, result);
        return result;
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyArg element, IStructure<PyFileRepresentative> arg) {
        PyVariableRepresentative result = new PyVariableRepresentative(element.getArg());

        if (element.getAnnotation() != null) {
            result.setType(new PyTypeParameterConstructorVisitor(element.getAnnotation()).getResult());
        }

        return List.of(result);
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyAssign element, IStructure<PyFileRepresentative> arg) {
        List<IStructure<PyFileRepresentative>> result = new ArrayList<>();

        boolean isInit = arg instanceof PyFuncRepresentative && arg.getName().equals(PySpecificFunction.INIT.getName());
        element.getTargets().forEach(s -> {
            PyVariableConstructorVisitor varsConstructor = new PyVariableConstructorVisitor(s,
                    isInit ? ((PyFuncRepresentative) arg).getArgNames().get(0) : null);

            result.addAll(varsConstructor.getResult());
            if (isInit) {
                System.out.println(arg.getName());
                processChildren(arg.getParent(), varsConstructor.getClassVars());
            }
        });

        return result;
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyAugAssign element, IStructure<PyFileRepresentative> arg) {
        List<IStructure<PyFileRepresentative>> result = new ArrayList<>();

        boolean isInit = arg instanceof PyFuncRepresentative && arg.getName().equals(PySpecificFunction.INIT.getName());

        PyVariableConstructorVisitor varsConstructor = new PyVariableConstructorVisitor(element.getTarget(),
                isInit ? ((PyFuncRepresentative) arg).getArgNames().get(0) : null);
        result.addAll(varsConstructor.getResult());
        if (isInit) {
            processChildren(arg.getParent(), varsConstructor.getClassVars());
        }

        return result;
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyAnnAssign element, IStructure<PyFileRepresentative> arg) {
        List<IStructure<PyFileRepresentative>> result = new ArrayList<>();

        boolean isInit = arg instanceof PyFuncRepresentative && arg.getName().equals(PySpecificFunction.INIT.getName());

        PyVariableConstructorVisitor varsConstructor = new PyVariableConstructorVisitor(element.getTarget(),
                isInit ? ((PyFuncRepresentative) arg).getArgNames().get(0) : null);

        PyType type = new PyTypeParameterConstructorVisitor(element.getAnnotation()).getResult();
        if (!varsConstructor.getResult().isEmpty()) {
            result.add(varsConstructor.getResult().get(0).setType(type));
        } else {
            if (isInit) {
                processChildren(arg.getParent(), List.of(varsConstructor.getClassVars().get(0).setType(type)));
            }
        }

        return result;
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyImport element, IStructure<PyFileRepresentative> arg) {
        mai.student.intermediateStates.python.PyImport result = new mai.student.intermediateStates.python.PyImport();
        result.setModule(Path.of(element.getNames().get(0).getName().replaceAll("\\.", "\\\\") + ".py"));
        result.setAlias(element.getNames().get(0).getAsname());
        ((PyFileRepresentative) arg).getImports().add(result);

        return new ArrayList<>();
    }

    @Override
    public List<IStructure<PyFileRepresentative>> visit(PyImportFrom element, IStructure<PyFileRepresentative> arg) {
        mai.student.intermediateStates.python.PyImport result = new mai.student.intermediateStates.python.PyImport();
        result.setModule(Path.of(element.getModule().replaceAll("\\.", "\\\\") + ".py"));
        result.setStatic(true);
        result.setEntity(element.getNames().get(0).getName());
        result.setAlias(element.getNames().get(0).getAsname());
        ((PyFileRepresentative) arg).getImports().add(result);

        return new ArrayList<>();
    }

    // Единая обработка потомков для всех типов
    private void processChildren(IStructure<PyFileRepresentative> target, List<? extends IStructure<PyFileRepresentative>> children) {
        if (children == null || children.isEmpty()) {
            return;
        }

        ArrayList<PyClassRepresentation> classes = new ArrayList<>();
        ArrayList<PyFuncRepresentative> functions = new ArrayList<>();
        ArrayList<PyVariableRepresentative> vars = new ArrayList<>();

        // Children separation
        for (IStructure<PyFileRepresentative> child : children) {
            switch (child.getStrucType()) {
                case Class:
                    classes.add(((PyClassRepresentation) child).setParent(target));
                    break;
                case Function:
                    functions.add(((PyFuncRepresentative) child).setParent(target));
                    break;
                case Variable:
                    vars.add(((PyVariableRepresentative) child).setParent(target));
                    break;
                default:
                    throw new UnsupportedOperationException("AnalysisVisitor.processChildren: unsupported child type!");
            }
        }

        // Adding children to target
        switch (target.getStrucType()) {
            case Class:
                PyClassRepresentation cl = (PyClassRepresentation) target;
                cl.classes.addAll(classes);
                cl.functions.addAll(functions);
                cl.variables.addAll(vars);
                break;
            case Function:
                PyFuncRepresentative fu = (PyFuncRepresentative) target;
                fu.classes.addAll(classes);
                fu.functions.addAll(functions);
                fu.variables.addAll(vars);
                break;
            case File:
                PyFileRepresentative fi = (PyFileRepresentative) target;
                fi.classes.addAll(classes);
                fi.functions.addAll(functions);
                fi.variables.addAll(vars);
                break;
            default:
                throw new UnsupportedOperationException("AnalysisVisitor.processChildren: unsupported target type!");
        }
    }
}
