package org.jpc.emulator.execution.opcodes.vm;

import org.jpc.emulator.execution.*;
import org.jpc.emulator.execution.decoder.*;
import org.jpc.emulator.processor.*;
import org.jpc.emulator.processor.fpu64.*;
import static org.jpc.emulator.processor.Processor.*;

public class call_o16_Ew_mem extends Executable
{
    final Pointer op1;
    final int blockLength;
    final int instructionLength;

    public call_o16_Ew_mem(int blockStart, Instruction parent)
    {
        super(blockStart, parent);
        blockLength = parent.x86Length+(int)parent.eip-blockStart;
        instructionLength = parent.x86Length;
        op1 = new Pointer(parent.operand[0], parent.adr_mode);
    }

    public Branch execute(Processor cpu)
    {
        cpu.eip += blockLength;
        if (((0xffff & cpu.r_sp.get16()) < 2) && ((cpu.r_esp.get16() & 0xffff) > 0))
	    throw ProcessorException.STACK_SEGMENT_0;
        int target = op1.get16(cpu);
        cpu.push16((short)cpu.eip);
        cpu.eip = 0xffff & target;
        return Branch.Call_Unknown;
    }

    public boolean isBranch()
    {
        return true;
    }

    public String toString()
    {
        return this.getClass().getName();
    }
}