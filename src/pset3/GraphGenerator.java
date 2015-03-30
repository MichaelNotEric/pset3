package pset3;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;

public class GraphGenerator {
	public CFG createCFG(String className) throws ClassNotFoundException {
		CFG cfg = new CFG();
		JavaClass jc = Repository.lookupClass(className);
		ClassGen cg = new ClassGen(jc);
		ConstantPoolGen cpg = cg.getConstantPool();
		
		for (Method m: cg.getMethods()) {
			//add dummy node for return
			cfg.addNode(-1, m, jc);
			MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
			InstructionList il = mg.getInstructionList();
			InstructionHandle[] handles = il.getInstructionHandles();
			for (InstructionHandle ih: handles) {
				int position = ih.getPosition();
				cfg.addNode(position, m, jc);
				Instruction inst = ih.getInstruction();
				// your code goes here
				String iname = inst.getName();
				if(iname.contains("load") || iname.contains("invokespecial")){
					//System.out.println("Found a load or invokespecial");
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);
				} else if(iname.contains("if")){
					//System.out.println("Found an if");
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);//if branch isn't taken
					BranchInstruction br = (BranchInstruction) inst;
					int dest = br.getTarget().getPosition();
					cfg.addEdge(position, m, jc, dest, m, jc);//if branch is taken
				} else if(iname.contains("return")){
					//System.out.println("Found a return");
					cfg.addEdge(position, m, jc, -1, m, jc);
				} else if(iname.contains("goto")){
					InstructionHandle nextih = ih.getNext();
					BranchInstruction br = (BranchInstruction) inst;
					int dest = br.getTarget().getPosition();
					cfg.addEdge(position, m, jc, dest, m, jc);
				} else{
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);
				}
				
				
				
			}
		}
		return cfg;
	}
	public CFG createCFGWithMethodInvocation(String className) throws ClassNotFoundException {
		// your code goes here
		CFG cfg = new CFG();
		JavaClass jc = Repository.lookupClass(className);
		ClassGen cg = new ClassGen(jc);
		ConstantPoolGen cpg = cg.getConstantPool();
		for (Method m: cg.getMethods()) {
			cfg.addNode(-1, m, jc);
			MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
			InstructionList il = mg.getInstructionList();
			InstructionHandle[] handles = il.getInstructionHandles();
			for (InstructionHandle ih: handles) {
				int position = ih.getPosition();
				cfg.addNode(position, m, jc);
				Instruction inst = ih.getInstruction();
				String iname = inst.getName();
				if(iname.contains("load") || iname.contains("invokespecial")){
					//System.out.println("Found a load or invokespecial");
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);
				} else if(iname.contains("if")){
					//System.out.println("Found an if");
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);//if branch isn't taken
					BranchInstruction br = (BranchInstruction) inst;
					int dest = br.getTarget().getPosition();
					cfg.addEdge(position, m, jc, dest, m, jc);//if branch is taken
				} else if(iname.contains("return")){
					//System.out.println("Found a return");
					cfg.addEdge(position, m, jc, -1, m, jc);
				} else if(iname.contains("invokestatic")){					
					//System.out.println("Found a method invocation!!!!!!!!!!!!!!!");	
					InvokeInstruction in = (InvokeInstruction) inst;
					Method nextm = (cg.containsMethod(in.getMethodName(cpg), in.getSignature(cpg)));
					JavaClass nextjc = (Repository.lookupClass(in.getClassName(cpg)));
					cfg.addEdge(position, m, jc, 0, nextm, nextjc);//invocation edge
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(-1, nextm, nextjc, nextPos, m, jc);//return edge
				} else if(iname.contains("goto")){
					InstructionHandle nextih = ih.getNext();
					BranchInstruction br = (BranchInstruction) inst;
					int dest = br.getTarget().getPosition();
					cfg.addEdge(position, m, jc, dest, m, jc);
				} else{
					InstructionHandle nextih = ih.getNext();
					int nextPos = nextih.getPosition();
					cfg.addEdge(position, m, jc, nextPos, m, jc);
				}
			}
		}
		return cfg;
	}
	public static void main(String[] a) throws ClassNotFoundException {
		GraphGenerator gg = new GraphGenerator();
		gg.createCFG("pset3.C"); // example invocation of createCFG
		gg.createCFGWithMethodInvocation("pset3.D"); // example invocation of createCFGWithMethodInovcation
	}
	
	
}