package com.awakenedredstone.sakuracake.nbt;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtNull implements NbtElement {
    public static final NbtNull INSTANCE = new NbtNull();
    public static final NbtType<NbtNull> TYPE = new NbtType.OfFixedSize<>() {
        public NbtNull read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) {
            return INSTANCE;
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) {
            return NbtScanner.Result.CONTINUE;
        }

        @Override
        public int getSizeInBytes() {
            return 0;
        }

        @Override
        public String getCrashReportName() {
            return "NULL";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Null";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }
    };

    private NbtNull() {}

    @Override
    public void write(DataOutput output) {}

    @Override
    public byte getType() {
        return Byte.MAX_VALUE;
    }

    @Override
    public NbtType<?> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtElement copy() {
        return INSTANCE;
    }

    @Override
    public int getSizeInBytes() {
        return 0;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {}

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return NbtScanner.Result.CONTINUE;
    }
}
