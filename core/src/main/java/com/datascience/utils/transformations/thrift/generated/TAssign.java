/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.datascience.utils.transformations.thrift.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TAssign implements org.apache.thrift.TBase<TAssign, TAssign._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TAssign");

  private static final org.apache.thrift.protocol.TField WORKER_FIELD_DESC = new org.apache.thrift.protocol.TField("worker", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField OBJECT_FIELD_DESC = new org.apache.thrift.protocol.TField("object", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField LABEL_FIELD_DESC = new org.apache.thrift.protocol.TField("label", org.apache.thrift.protocol.TType.STRUCT, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TAssignStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TAssignTupleSchemeFactory());
  }

  public TWorker worker; // required
  public String object; // required
  public TLabel label; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    WORKER((short)1, "worker"),
    OBJECT((short)2, "object"),
    LABEL((short)3, "label");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // WORKER
          return WORKER;
        case 2: // OBJECT
          return OBJECT;
        case 3: // LABEL
          return LABEL;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.WORKER, new org.apache.thrift.meta_data.FieldMetaData("worker", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TWorker.class)));
    tmpMap.put(_Fields.OBJECT, new org.apache.thrift.meta_data.FieldMetaData("object", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LABEL, new org.apache.thrift.meta_data.FieldMetaData("label", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLabel.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TAssign.class, metaDataMap);
  }

  public TAssign() {
  }

  public TAssign(
    TWorker worker,
    String object,
    TLabel label)
  {
    this();
    this.worker = worker;
    this.object = object;
    this.label = label;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TAssign(TAssign other) {
    if (other.isSetWorker()) {
      this.worker = new TWorker(other.worker);
    }
    if (other.isSetObject()) {
      this.object = other.object;
    }
    if (other.isSetLabel()) {
      this.label = new TLabel(other.label);
    }
  }

  public TAssign deepCopy() {
    return new TAssign(this);
  }

  @Override
  public void clear() {
    this.worker = null;
    this.object = null;
    this.label = null;
  }

  public TWorker getWorker() {
    return this.worker;
  }

  public TAssign setWorker(TWorker worker) {
    this.worker = worker;
    return this;
  }

  public void unsetWorker() {
    this.worker = null;
  }

  /** Returns true if field worker is set (has been assigned a value) and false otherwise */
  public boolean isSetWorker() {
    return this.worker != null;
  }

  public void setWorkerIsSet(boolean value) {
    if (!value) {
      this.worker = null;
    }
  }

  public String getObject() {
    return this.object;
  }

  public TAssign setObject(String object) {
    this.object = object;
    return this;
  }

  public void unsetObject() {
    this.object = null;
  }

  /** Returns true if field object is set (has been assigned a value) and false otherwise */
  public boolean isSetObject() {
    return this.object != null;
  }

  public void setObjectIsSet(boolean value) {
    if (!value) {
      this.object = null;
    }
  }

  public TLabel getLabel() {
    return this.label;
  }

  public TAssign setLabel(TLabel label) {
    this.label = label;
    return this;
  }

  public void unsetLabel() {
    this.label = null;
  }

  /** Returns true if field label is set (has been assigned a value) and false otherwise */
  public boolean isSetLabel() {
    return this.label != null;
  }

  public void setLabelIsSet(boolean value) {
    if (!value) {
      this.label = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case WORKER:
      if (value == null) {
        unsetWorker();
      } else {
        setWorker((TWorker)value);
      }
      break;

    case OBJECT:
      if (value == null) {
        unsetObject();
      } else {
        setObject((String)value);
      }
      break;

    case LABEL:
      if (value == null) {
        unsetLabel();
      } else {
        setLabel((TLabel)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case WORKER:
      return getWorker();

    case OBJECT:
      return getObject();

    case LABEL:
      return getLabel();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case WORKER:
      return isSetWorker();
    case OBJECT:
      return isSetObject();
    case LABEL:
      return isSetLabel();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TAssign)
      return this.equals((TAssign)that);
    return false;
  }

  public boolean equals(TAssign that) {
    if (that == null)
      return false;

    boolean this_present_worker = true && this.isSetWorker();
    boolean that_present_worker = true && that.isSetWorker();
    if (this_present_worker || that_present_worker) {
      if (!(this_present_worker && that_present_worker))
        return false;
      if (!this.worker.equals(that.worker))
        return false;
    }

    boolean this_present_object = true && this.isSetObject();
    boolean that_present_object = true && that.isSetObject();
    if (this_present_object || that_present_object) {
      if (!(this_present_object && that_present_object))
        return false;
      if (!this.object.equals(that.object))
        return false;
    }

    boolean this_present_label = true && this.isSetLabel();
    boolean that_present_label = true && that.isSetLabel();
    if (this_present_label || that_present_label) {
      if (!(this_present_label && that_present_label))
        return false;
      if (!this.label.equals(that.label))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TAssign other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TAssign typedOther = (TAssign)other;

    lastComparison = Boolean.valueOf(isSetWorker()).compareTo(typedOther.isSetWorker());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWorker()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.worker, typedOther.worker);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetObject()).compareTo(typedOther.isSetObject());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetObject()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.object, typedOther.object);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLabel()).compareTo(typedOther.isSetLabel());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLabel()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.label, typedOther.label);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TAssign(");
    boolean first = true;

    sb.append("worker:");
    if (this.worker == null) {
      sb.append("null");
    } else {
      sb.append(this.worker);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("object:");
    if (this.object == null) {
      sb.append("null");
    } else {
      sb.append(this.object);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("label:");
    if (this.label == null) {
      sb.append("null");
    } else {
      sb.append(this.label);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (worker != null) {
      worker.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TAssignStandardSchemeFactory implements SchemeFactory {
    public TAssignStandardScheme getScheme() {
      return new TAssignStandardScheme();
    }
  }

  private static class TAssignStandardScheme extends StandardScheme<TAssign> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TAssign struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // WORKER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.worker = new TWorker();
              struct.worker.read(iprot);
              struct.setWorkerIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // OBJECT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.object = iprot.readString();
              struct.setObjectIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // LABEL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.label = new TLabel();
              struct.label.read(iprot);
              struct.setLabelIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TAssign struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.worker != null) {
        oprot.writeFieldBegin(WORKER_FIELD_DESC);
        struct.worker.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.object != null) {
        oprot.writeFieldBegin(OBJECT_FIELD_DESC);
        oprot.writeString(struct.object);
        oprot.writeFieldEnd();
      }
      if (struct.label != null) {
        oprot.writeFieldBegin(LABEL_FIELD_DESC);
        struct.label.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TAssignTupleSchemeFactory implements SchemeFactory {
    public TAssignTupleScheme getScheme() {
      return new TAssignTupleScheme();
    }
  }

  private static class TAssignTupleScheme extends TupleScheme<TAssign> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TAssign struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetWorker()) {
        optionals.set(0);
      }
      if (struct.isSetObject()) {
        optionals.set(1);
      }
      if (struct.isSetLabel()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetWorker()) {
        struct.worker.write(oprot);
      }
      if (struct.isSetObject()) {
        oprot.writeString(struct.object);
      }
      if (struct.isSetLabel()) {
        struct.label.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TAssign struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.worker = new TWorker();
        struct.worker.read(iprot);
        struct.setWorkerIsSet(true);
      }
      if (incoming.get(1)) {
        struct.object = iprot.readString();
        struct.setObjectIsSet(true);
      }
      if (incoming.get(2)) {
        struct.label = new TLabel();
        struct.label.read(iprot);
        struct.setLabelIsSet(true);
      }
    }
  }

}
