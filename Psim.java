import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Psim {

	static class Instruction {  //class structure for instruction tokens
		
		public String opcode;
		public String destination_register;
		public String first_source_operand;
		public int first_source_operand_int;
		public String second_source_operand;
		public int second_source_operand_int;
		public int instruction_order; //Added in order to keep tract of instruction order
		
		public Instruction() { //Default Constructor
			this.opcode = "";
			this.destination_register = "";
			this.first_source_operand = "";
			this.first_source_operand_int = 0;
			this.second_source_operand = "";
			this.second_source_operand_int = 0;
			this.instruction_order = 0;
			
		}
		
		public Instruction(String op, String dest, String first, String second, int order) { //Constructor for arithmetic instructions
			this.opcode = op;
			this.destination_register = dest;
			this.first_source_operand = first;
			this.first_source_operand_int = 0;
			this.second_source_operand = second;
			this.second_source_operand_int = 0;
			this.instruction_order = order;
			
		}
		public Instruction(String op, String dest, String first, int second, int order) { ////Constructor for store instructions
			this.opcode = op;
			this.destination_register = dest;
			this.first_source_operand = first;
			this.first_source_operand_int = 0;
			this.second_source_operand = "";
			this.second_source_operand_int = second;
			this.instruction_order = order;
		}
		public Instruction(String op, String dest, int first, int second, int order) { ////Constructor for modified value instructions
			this.opcode = op;
			this.destination_register = dest;
			this.first_source_operand = "";
			this.first_source_operand_int = first;
			this.second_source_operand = "";
			this.second_source_operand_int = second;
			this.instruction_order = order;
		}
		
		public Instruction (Instruction old) { //For deep copying of an instruction
			this.opcode = old.opcode;
			this.destination_register = old.destination_register;
			this.first_source_operand = old.first_source_operand;
			this.first_source_operand_int = old.first_source_operand_int;
			this.second_source_operand = old.second_source_operand;
			this.second_source_operand_int = old.second_source_operand_int;
			this.instruction_order = old.instruction_order;
		}
		
		
	}

	
	static class Register {  //class structure for register tokens
		
		public String register_name;
		public int register_value;
		public int register_order; //Value refers to the original instruction that this register was instantiated from
		
		public Register() { //Default Constructor
			this.register_name = "";
			this.register_value = 0;
			this.register_order = 0;
			
		}
		
		public Register(String name, int value, int order) {
			this.register_name = name;
			this.register_value = value;
			this.register_order = order;
			
		}
		
		public Register (Register old) { //For deep copying of an register
			this.register_name = old.register_name;
			this.register_value = old.register_value;
			this.register_order = old.register_order;
		}
		
	}
	
	
	static class DataMemory {  //class structure for DataMemory tokens
		
		public int address;
		public int value;
		
		public DataMemory() { //Default Constructor
			this.address = 0;
			this.value = 0;
			
		}
		
		public DataMemory(int address, int value) {
			this.address = address;
			this.value = value;
		}
		
		public DataMemory (DataMemory old) { //For deep copying of an data memory
			this.address = old.address;
			this.value = old.value;
			
		}
	}
	
	
	//Creating static ArrayLists for each place inside of PetriNet
	//Represents current state of the system
	static ArrayList<Instruction> INM = new ArrayList<Instruction>();
	static ArrayList<Instruction> INB = new ArrayList<Instruction>();
	static ArrayList<Instruction> AIB = new ArrayList<Instruction>();
	static ArrayList<Instruction> SIB = new ArrayList<Instruction>();
	static ArrayList<Instruction> PRB = new ArrayList<Instruction>();
	static ArrayList<Register> ADB = new ArrayList<Register>();
	static ArrayList<Register> REB = new ArrayList<Register>();
	static ArrayList<Register> RGF = new ArrayList<Register>();
	static ArrayList<DataMemory> DAM = new ArrayList<DataMemory>();
	
	
	//Creating ArrayLists for next_state of Petri Net
	//The purpose of these next_state ArrayLists is to hold the results of 
	//transitions. We cannot do multiple transitions in parallel, but we can
	//hold the effects of them and then transition into the next state all together,
	//effectively simulating it happening simultaneously.
	static ArrayList<Instruction> next_state_INM = new ArrayList<Instruction>();
	static ArrayList<Instruction> next_state_INB = new ArrayList<Instruction>();
	static ArrayList<Instruction> next_state_AIB = new ArrayList<Instruction>();
	static ArrayList<Instruction> next_state_SIB = new ArrayList<Instruction>();
	static ArrayList<Instruction> next_state_PRB = new ArrayList<Instruction>();
	static ArrayList<Register> next_state_ADB = new ArrayList<Register>();
	static ArrayList<Register> next_state_REB = new ArrayList<Register>();
	static ArrayList<Register> next_state_RGF = new ArrayList<Register>();
	static ArrayList<DataMemory> next_state_DAM = new ArrayList<DataMemory>();
	
	
	static void importInstructions(String path) {  //Function for reading in input file for importing instructions
				
		File file = new File(path);
		
		try {
			Scanner sc = new Scanner(file);
			
			int instruction_count = 0;
			while(sc.hasNextLine()) {  //Reading through each line of the txt file
				
				String currLine = sc.nextLine();		//Getting each line
				int length = currLine.length();		
				
				currLine = currLine.substring(1,length-1); //Removing < and > at beginning and end
				
				String[] operands = currLine.split(",");  //Splitting up based on comma
								
				if(operands[3].contains("R")) { //arithmetic instruction
					Instruction instruction = new Instruction(operands[0],operands[1],operands[2],operands[3], instruction_count);
					instruction_count++;
					INM.add(instruction); //Storing instruction into arrayList
				}
				else { //Store Instruction
					Instruction instruction = new Instruction(operands[0],operands[1],operands[2],Integer.parseInt(operands[3]),instruction_count); //Converting second source operand into an integer
					instruction_count++;
					INM.add(instruction); //Storing instruction into arrayList
				}	
			}
			sc.close();
		}
		catch(Exception e) {
			System.out.println("Incorrect File Path");
		}
		
	}
	
	
	static void importRegisters(String path){	   //Function for reading in input file for importing registers
		
		File file = new File(path);
				
				try {
					Scanner sc = new Scanner(file);
					
					while(sc.hasNextLine()) {  //Reading through each line of the txt file
						
						String currLine = sc.nextLine();		//Getting each line
						int length = currLine.length();		
						
						currLine = currLine.substring(1,length-1); //Removing < and > at beginning and end
						
						String[] operands = currLine.split(",");  //Splitting up based on comma
						
						Register register = new Register(operands[0],Integer.parseInt(operands[1]), 0); //Set register order to 0 because it will not be used
						RGF.add(register);
					}
					sc.close();
				}
				catch(Exception e) {
					System.out.println("Incorrect File Path");
				}
	}
	
	
	static void importDataMemory(String path){ 	   //Function for reading in input file for importing data memory
		

		File file = new File(path);
				
				try {
					Scanner sc = new Scanner(file);
					
					while(sc.hasNextLine()) {  //Reading through each line of the txt file
						
						String currLine = sc.nextLine();		//Getting each line
						int length = currLine.length();		
						
						currLine = currLine.substring(1,length-1); //Removing < and > at beginning and end
						
						String[] operands = currLine.split(",");  //Splitting up based on comma
						
						DataMemory data = new DataMemory(Integer.parseInt(operands[0]),Integer.parseInt(operands[1]));
						DAM.add(data);
					}
					sc.close();
				}
				catch(Exception e) {
					System.out.println("Incorrect File Path");
				}
		
	}
	
	
	static String print(int step) {           //Function for printing out state of current step
		
		String output = "";
		
		if(step == 0) { //Do not do initial "next line" if it is the first step
			output = output + "STEP " + step + ":";
		}
		else {
			output = output + "\nSTEP " + step + ":";
		}

		//Printing State of INM------------------------------------------------------
		output = output + "\nINM:";
		for(int i = 0; i < INM.size(); i++) {
			Instruction instruction = INM.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + instruction.opcode + ",";
			
			output = output + instruction.destination_register + ",";
			
			if (instruction.first_source_operand == "") {
					output = output + instruction.first_source_operand_int + ",";
			}
			else {
				output = output + instruction.first_source_operand + ",";

			}
			if (instruction.second_source_operand == "") {
				output = output + instruction.second_source_operand_int + ">";
			}
			else {
				output = output + instruction.second_source_operand + ">";
			}
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of INB------------------------------------------------------
		output = output + "\nINB:";
		for(int i = 0; i < INB.size(); i++) {
			Instruction instruction = INB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + instruction.opcode + ",";
			
			output = output + instruction.destination_register + ",";
			
			if (instruction.first_source_operand == "") {
					output = output + instruction.first_source_operand_int + ",";
			}
			else {
				output = output + instruction.first_source_operand + ",";

			}
			if (instruction.second_source_operand == "") {
				output = output + instruction.second_source_operand_int + ">";
			}
			else {
				output = output + instruction.second_source_operand + ">";
			}
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of AIB------------------------------------------------------
		output = output + "\nAIB:";
		for(int i = 0; i < AIB.size(); i++) {
			Instruction instruction = AIB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + instruction.opcode + ",";
			
			output = output + instruction.destination_register + ",";
			
			if (instruction.first_source_operand == "") {
					output = output + instruction.first_source_operand_int + ",";
			}
			else {
				output = output + instruction.first_source_operand + ",";

			}
			if (instruction.second_source_operand == "") {
				output = output + instruction.second_source_operand_int + ">";
			}
			else {
				output = output + instruction.second_source_operand + ">";
			}
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of INB------------------------------------------------------
		output = output + "\nSIB:";
		for(int i = 0; i < SIB.size(); i++) {
			Instruction instruction = SIB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + instruction.opcode + ",";
			
			output = output + instruction.destination_register + ",";
			
			if (instruction.first_source_operand == "") {
					output = output + instruction.first_source_operand_int + ",";
			}
			else {
				output = output + instruction.first_source_operand + ",";

			}
			if (instruction.second_source_operand == "") {
				output = output + instruction.second_source_operand_int + ">";
			}
			else {
				output = output + instruction.second_source_operand + ">";
			}
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of PRB------------------------------------------------------
		output = output + "\nPRB:";
		for(int i = 0; i < PRB.size(); i++) {
			Instruction instruction = PRB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + instruction.opcode + ",";
			
			output = output + instruction.destination_register + ",";
			
			if (instruction.first_source_operand == "") {
					output = output + instruction.first_source_operand_int + ",";
			}
			else {
				output = output + instruction.first_source_operand + ",";

			}
			if (instruction.second_source_operand == "") {
				output = output + instruction.second_source_operand_int + ">";
			}
			else {
				output = output + instruction.second_source_operand + ">";
			}
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of ADB------------------------------------------------------
		output = output + "\nADB:";
		for(int i = 0; i < ADB.size(); i++) {
			Register register = ADB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + register.register_name + "," + register.register_value + ">";	
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of REB------------------------------------------------------
		output = output + "\nREB:";
		for(int i = 0; i < REB.size(); i++) {
			Register register = REB.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + register.register_name + "," + register.register_value + ">";	
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of RGF------------------------------------------------------
		output = output + "\nRGF:";
		for(int i = 0; i < RGF.size(); i++) {
			Register register = RGF.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + register.register_name + "," + register.register_value + ">";	
		}
		//Done printing state--------------------------------------------------------------
		//Printing State of DAM------------------------------------------------------
		output = output + "\nDAM:";
		for(int i = 0; i < DAM.size(); i++) {
			DataMemory data_memory = DAM.get(i);
			
			if (i != 0) { //Adding commas between entries
				output = output + ",";
			}
			//Converting Token data into a string.
			output = output + "<" + data_memory.address + "," + data_memory.value + ">";	
		}
		//Done printing state--------------------------------------------------------------
		
		output = output + "\n";
		
		return output;
		
	}
	
	
	static ArrayList<Instruction> cloneInstructionList(ArrayList<Instruction> old_list) {  //Function that is essentially a "Deep Copy." Java doesn't have a native deep copy.
		
		ArrayList<Instruction> cloned_list = new ArrayList<Instruction>();
		
		for (int i = 0 ; i < old_list.size(); i++) {
			Instruction old = old_list.get(i);
			Instruction clone = new Instruction();
			clone.opcode = old.opcode;
			
			String dest = old.destination_register;
			String first = old.first_source_operand;
			int first_int = old.first_source_operand_int;
			String second = old.second_source_operand;
			int second_int = old.second_source_operand_int;
			int order = old.instruction_order;
			
			clone.destination_register = dest;
			clone.first_source_operand = first;
			clone.first_source_operand_int = first_int;
			clone.second_source_operand = second;
			clone.second_source_operand_int = second_int;
			clone.instruction_order = order;
			cloned_list.add(clone);
		}
		return cloned_list; 
	}
	
	static ArrayList<Register> cloneRegisterList(ArrayList<Register> old_list) {  //Function that is essentially a "Deep Copy." Java doesn't have a native deep copy.
		
		ArrayList<Register> cloned_list = new ArrayList<Register>();
		
		for (int i = 0 ; i < old_list.size(); i++) {
			Register old = old_list.get(i);
			Register clone = new Register();
			clone.register_name = old.register_name;
			clone.register_value = old.register_value;
			clone.register_order = old.register_order;
			cloned_list.add(clone);
		}
		return cloned_list; 
	}
	
	static ArrayList<DataMemory> cloneDataList(ArrayList<DataMemory> old_list) {  //Function that is essentially a "Deep Copy." Java doesn't have a native deep copy.
		
		ArrayList<DataMemory> cloned_list = new ArrayList<DataMemory>();
		
		for (int i = 0 ; i < old_list.size(); i++) {
			DataMemory old = old_list.get(i);
			DataMemory clone = new DataMemory();
			clone.address = old.address;
			clone.value = old.value;
			cloned_list.add(clone);
		}
		return cloned_list; 
	}
	
	static void store_REB(Register register) { //Function for storing registers in RGF, accounting for instruction order.
		
		int new_order = register.register_order;
		
		if (next_state_REB.isEmpty()){ //Just add it if it is empty
			next_state_REB.add(register);
			return;
		}
		
		int added = 0; //Flag for whether or not the token as been added.
		for(int i = 0; i < next_state_REB.size(); i++) {
			
			int curr_order = next_state_REB.get(i).register_order; //gets order value of register in REB
			
			if(new_order < curr_order) { //Adds register in order if it does not exist
				next_state_REB.add(i, register);
				added = 1;
				break;
			}
		}
		
		if (added == 0) { //If it goes through list, and is not added, the it is appended to the end.
			next_state_REB.add(register);
		}
		
	}
	
	static void store_register(Register register){    //Function for storing registers in RGF, accounting for in-orderness and replacements.
		
		if (next_state_RGF.isEmpty()){ //Just add it if it is empty
			next_state_RGF.add(register);
			return;
		}
		
		
		String name = register.register_name;
		int new_index = Integer.parseInt(name.substring(1));
		int new_value = register.register_value;
		
		int added = 0; //Flag for whether or not the token as been added.
		for(int i = 0; i < next_state_RGF.size(); i++) {
			
			int curr_index = Integer.parseInt(next_state_RGF.get(i).register_name.substring(1)); //gets index value of register in RGF
			
			if (curr_index == new_index) { //Replaces value of register if register already exists
				next_state_RGF.get(i).register_value = new_value;
				added = 1;
				break;
			}
			if(new_index < curr_index) { //Adds register in order if it does not exist
				next_state_RGF.add(i, register);
				added = 1;
				break;
			}
		}
		
		if (added == 0) { //If it goes through list, and is not added, the it is appended to the end.
			next_state_RGF.add(register);
		}
	}
	
	static void store_data(DataMemory data_memory){    //Function for storing data in DAM, accounting for in-orderness and replacements.
		
		if (next_state_DAM.isEmpty()){ //Just add it if it is empty
			next_state_DAM.add(data_memory);
			return;
		}
		
		int new_addr = data_memory.address;
		int new_value = data_memory.value;
		
		int added = 0; //Flag for whether or not the token as been added.
		for(int i = 0; i < next_state_DAM.size(); i++) {
			
			int curr_addr = next_state_DAM.get(i).address; //gets index value of register in RGF
			
			if (curr_addr == new_addr) { //Replaces value of register if register already exists
				next_state_DAM.get(i).value = new_value;
				added = 1;
				break;
			}
			if(new_addr < curr_addr) { //Adds register in order if it does not exist
				next_state_DAM.add(i, data_memory);
				added = 1;
				break;
			}
		}
		
		if (added == 0) { //If it goes through list, and is not added, the it is appended to the end.
			next_state_DAM.add(data_memory);
		}
		
	}

	
	static void step() { //This function "steps" forward. Next states become current states.
		
		//clone*List creates deep copy. This avoids issue where both next_state and current state refers to same object.
		ADB = cloneRegisterList(next_state_ADB);
		AIB = cloneInstructionList(next_state_AIB);
		DAM = cloneDataList(next_state_DAM);
		INB = cloneInstructionList(next_state_INB);
		INM = cloneInstructionList(next_state_INM);
		PRB = cloneInstructionList(next_state_PRB);
		REB = cloneRegisterList(next_state_REB);
		RGF = cloneRegisterList(next_state_RGF);
		SIB = cloneInstructionList(next_state_SIB);	
	}
	
	
	static boolean isDone() {  //This Function tells you when the Petri Net is finished executing
		
		//Determined when all states and next states are empty, minus RGF and DAM
		
		//If all states minus next/current state RGF and DAM are empty
		if(INM.isEmpty() && INB.isEmpty() && AIB.isEmpty() && SIB.isEmpty() && PRB.isEmpty() && ADB.isEmpty() && REB.isEmpty() && next_state_INM.isEmpty() && next_state_INB.isEmpty() && next_state_AIB.isEmpty() && next_state_SIB.isEmpty() && next_state_PRB.isEmpty() && next_state_ADB.isEmpty() && next_state_REB.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	//The below functions detail the different transition phases.
	
	static void decode_and_read(){
		
		//Get next instruction
		Instruction instruction = INM.get(0);
		next_state_INM.remove(0);
		
		Instruction modified_instruction = new Instruction(instruction);
		
		//Following if statements read the register name inside of instruction and replaces it with a number value.
		if (modified_instruction.first_source_operand != "") {
			for(int i = 0; i < RGF.size(); i++) {
				Register register = RGF.get(i);
				if(register.register_name.equals(modified_instruction.first_source_operand)) {
					modified_instruction.first_source_operand = "";
					modified_instruction.first_source_operand_int = register.register_value;
				}
			}
		}
		if (modified_instruction.second_source_operand != "") {
			for(int i = 0; i < RGF.size(); i++) {
				Register register = RGF.get(i);
				if(register.register_name.equals(modified_instruction.second_source_operand)) {
					modified_instruction.second_source_operand = "";
					modified_instruction.second_source_operand_int = register.register_value;
				}
			}
			
		}
		
		next_state_INB.add(modified_instruction);
	}
	
	static void issue_one() {
		
		//Getting token from INB
		Instruction instruction = INB.get(0);
		next_state_INB.remove(0);
		
		//Adding it to AIB
		Instruction clone = new Instruction(instruction);
		next_state_AIB.add(clone);
		
	}
	
	static void issue_two() {
		
		//Getting token from INB
		Instruction instruction = INB.get(0);
		next_state_INB.remove(0);
		
		//Adding it to SIB
		Instruction clone = new Instruction(instruction);
		next_state_SIB.add(clone);
		
	}
	
	static void asu() {
		
		//Get instruction token from AIB
		Instruction instruction = AIB.get(0);
		next_state_AIB.remove(0);
		
		int result;
		if (instruction.opcode.equals("ADD")){
			result = instruction.first_source_operand_int + instruction.second_source_operand_int;
		}
		else { // equals SUM
			result = instruction.first_source_operand_int - instruction.second_source_operand_int;
		}
		
		String register_name = instruction.destination_register;
		int order = instruction.instruction_order;
		
		//Creating register token for output
		Register register = new Register(register_name, result, order); //Adding order to register, so that if there are two in REB, they get executed properly
		store_REB(register);
	
	}
	
	static void mlu1() {
		
		//Getting token from AIB
		Instruction instruction = AIB.get(0);
		next_state_AIB.remove(0);
		
		Instruction clone = new Instruction(instruction);
		
		//Putting token into PRB
		next_state_PRB.add(clone);
		
	}
	
	static void mlu2() {
		
		//Getting instruction token from PRB
		Instruction instruction = PRB.get(0);
		next_state_PRB.remove(0);
		
		String register_name = instruction.destination_register;

		int order = instruction.instruction_order;
		
		int result = instruction.first_source_operand_int * instruction.second_source_operand_int;
		
		Register register = new Register(register_name,result, order); //Adding order to register, so that if there are two in REB, they get executed properly
		
		//Adding register token to REB
		store_REB(register);
	}
	
	static void addr() {
		
		//Get instruction token from SIB
		Instruction instruction = SIB.get(0);
		next_state_SIB.remove(0);
		
		//Doing Address calculation
		int address = instruction.first_source_operand_int + instruction.second_source_operand_int;
		String register_name = instruction.destination_register;
		
		//Creating new register token and storing it into ADB
		Register register = new Register(register_name, address, 0); //Set register order to 0 because it will not be used.
		next_state_ADB.add(register);
		
	}
	
	static void store() {
		
		//Get register token from SIB
		Register register = ADB.get(0);
		next_state_ADB.remove(0);
		
		int address = register.register_value;
		int value = -1;
		
		
		//Searching for Register in RGF
		for(int i = 0; i < RGF.size(); i++) {
			Register RGF_inst = RGF.get(i);
			if(RGF_inst.register_name.equals(register.register_name)) {
				value = RGF_inst.register_value;
			}
		}
		
		
		//Creating Data Memory Token
		DataMemory dataMemory = new DataMemory(address, value);
		store_data(dataMemory);
		
	}
	
	static void write() {
		
		//Getting Register token from REB
		Register register = REB.get(0);
		next_state_REB.remove(0);
		
		//deep copy
		Register clone = new Register(register);
		
		//Adding token to RGF
		store_register(clone);
	}
	
	public static void main(String[] args) {  //Main Method-------------------------------------------------------------------------

		//Uses local path
		importInstructions("instructions.txt");
		
		importRegisters("registers.txt");
				
		importDataMemory("datamemory.txt");
		
		//Copying import data into next_states
		next_state_DAM = cloneDataList(DAM);
		next_state_INM = cloneInstructionList(INM);
		next_state_RGF = cloneRegisterList(RGF);
		
		int step = 0;
		
		String output = "";
		
		boolean done = false;
		while(!(isDone() && done)) {
			
			done = isDone(); //Gives it one extra step after completion to show final state.
			
			if(!INM.isEmpty()) { //Skip if INM is empty
				decode_and_read();
			}
			
			if(!INB.isEmpty()) { //Skip if INB is empty
				
				if(INB.get(0).opcode.equals("ST")){
					issue_two();
				}
				else { //Arithmetic
					issue_one();
				}
			}
			
			if(!AIB.isEmpty()) { //Skip if AIB is empty
				
				if(AIB.get(0).opcode.equals("MUL")){ 
					mlu1();
				}
				else { //Add or Subtract
					asu();
				}
			}
			
			if(!PRB.isEmpty()) { //Skip if PRB is empty
				mlu2();
			}
			
			if(!SIB.isEmpty()) { //Skip if SIB is empty
				addr();
			}
			
			if(!ADB.isEmpty()) { //Skip if ADB is empty
				store();
			}
			
			if(!REB.isEmpty()) { //Skip if REB is empty
				write();
			}
			
			output = output + print(step);
			step++;
			step();
		}
		
		//After Petri Net is done, print output into a text file
		
		try {
		PrintWriter out = new PrintWriter("simulation.txt");
		out.print(output);
		out.close();
		}
		catch(Exception e) {
			System.out.print("File Error");
		}		
	}

}
