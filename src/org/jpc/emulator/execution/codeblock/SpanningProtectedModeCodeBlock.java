package org.jpc.emulator.execution.codeblock;

import org.jpc.emulator.processor.Processor;
import org.jpc.emulator.memory.AddressSpace;

class SpanningProtectedModeCodeBlock extends SpanningCodeBlock implements ProtectedModeCodeBlock
{
    private PeekableMemoryStream byteSourceStream = new PeekableMemoryStream();

    private CodeBlockFactory[] factories;
    private int length;
    private int decodes = 0;

    public SpanningProtectedModeCodeBlock(CodeBlockFactory[] factories)
    {
	    this.factories = factories;
    }

    public int getX86Length()
    {
        return length;
    }
    
    public CodeBlock decode(Processor cpu)
    {
        decodes++;
        ProtectedModeCodeBlock block = null;
        AddressSpace memory = cpu.linearMemory;
        int address = cpu.getInstructionPointer();
        boolean opSize = cpu.cs.getDefaultSizeFlag();
        for (int i = 0; (i < factories.length) && (block == null); i++) {
            try {
                byteSourceStream.set(memory, address);
                block = factories[i].getProtectedModeCodeBlock(byteSourceStream, opSize);
            } catch (IllegalStateException e) {e.printStackTrace();}
        }
        length = block.getX86Length();
        if (decodes % 1000 == 0)
            System.out.printf("PM Spanning block at %08x of length %d decoded %d times.\n", cpu.getInstructionPointer(), length, decodes);
        byteSourceStream.set(null, 0);
        return block;
    }
    
    public String toString()
    {
        return "Spanning Protected Mode CodeBlock";
    }
}