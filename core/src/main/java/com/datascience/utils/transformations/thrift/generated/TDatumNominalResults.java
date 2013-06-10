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

public class TDatumNominalResults implements org.apache.thrift.TBase<TDatumNominalResults, TDatumNominalResults._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TDatumNominalResults");

  private static final org.apache.thrift.protocol.TField CATEGORY_PROBABILITIES_FIELD_DESC = new org.apache.thrift.protocol.TField("categoryProbabilities", org.apache.thrift.protocol.TType.MAP, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TDatumNominalResultsStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TDatumNominalResultsTupleSchemeFactory());
  }

  public Map<String,Double> categoryProbabilities; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CATEGORY_PROBABILITIES((short)1, "categoryProbabilities");

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
        case 1: // CATEGORY_PROBABILITIES
          return CATEGORY_PROBABILITIES;
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
    tmpMap.put(_Fields.CATEGORY_PROBABILITIES, new org.apache.thrift.meta_data.FieldMetaData("categoryProbabilities", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TDatumNominalResults.class, metaDataMap);
  }

  public TDatumNominalResults() {
  }

  public TDatumNominalResults(
    Map<String,Double> categoryProbabilities)
  {
    this();
    this.categoryProbabilities = categoryProbabilities;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TDatumNominalResults(TDatumNominalResults other) {
    if (other.isSetCategoryProbabilities()) {
      Map<String,Double> __this__categoryProbabilities = new HashMap<String,Double>();
      for (Map.Entry<String, Double> other_element : other.categoryProbabilities.entrySet()) {

        String other_element_key = other_element.getKey();
        Double other_element_value = other_element.getValue();

        String __this__categoryProbabilities_copy_key = other_element_key;

        Double __this__categoryProbabilities_copy_value = other_element_value;

        __this__categoryProbabilities.put(__this__categoryProbabilities_copy_key, __this__categoryProbabilities_copy_value);
      }
      this.categoryProbabilities = __this__categoryProbabilities;
    }
  }

  public TDatumNominalResults deepCopy() {
    return new TDatumNominalResults(this);
  }

  @Override
  public void clear() {
    this.categoryProbabilities = null;
  }

  public int getCategoryProbabilitiesSize() {
    return (this.categoryProbabilities == null) ? 0 : this.categoryProbabilities.size();
  }

  public void putToCategoryProbabilities(String key, double val) {
    if (this.categoryProbabilities == null) {
      this.categoryProbabilities = new HashMap<String,Double>();
    }
    this.categoryProbabilities.put(key, val);
  }

  public Map<String,Double> getCategoryProbabilities() {
    return this.categoryProbabilities;
  }

  public TDatumNominalResults setCategoryProbabilities(Map<String,Double> categoryProbabilities) {
    this.categoryProbabilities = categoryProbabilities;
    return this;
  }

  public void unsetCategoryProbabilities() {
    this.categoryProbabilities = null;
  }

  /** Returns true if field categoryProbabilities is set (has been assigned a value) and false otherwise */
  public boolean isSetCategoryProbabilities() {
    return this.categoryProbabilities != null;
  }

  public void setCategoryProbabilitiesIsSet(boolean value) {
    if (!value) {
      this.categoryProbabilities = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CATEGORY_PROBABILITIES:
      if (value == null) {
        unsetCategoryProbabilities();
      } else {
        setCategoryProbabilities((Map<String,Double>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CATEGORY_PROBABILITIES:
      return getCategoryProbabilities();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CATEGORY_PROBABILITIES:
      return isSetCategoryProbabilities();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TDatumNominalResults)
      return this.equals((TDatumNominalResults)that);
    return false;
  }

  public boolean equals(TDatumNominalResults that) {
    if (that == null)
      return false;

    boolean this_present_categoryProbabilities = true && this.isSetCategoryProbabilities();
    boolean that_present_categoryProbabilities = true && that.isSetCategoryProbabilities();
    if (this_present_categoryProbabilities || that_present_categoryProbabilities) {
      if (!(this_present_categoryProbabilities && that_present_categoryProbabilities))
        return false;
      if (!this.categoryProbabilities.equals(that.categoryProbabilities))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TDatumNominalResults other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TDatumNominalResults typedOther = (TDatumNominalResults)other;

    lastComparison = Boolean.valueOf(isSetCategoryProbabilities()).compareTo(typedOther.isSetCategoryProbabilities());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCategoryProbabilities()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.categoryProbabilities, typedOther.categoryProbabilities);
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
    StringBuilder sb = new StringBuilder("TDatumNominalResults(");
    boolean first = true;

    sb.append("categoryProbabilities:");
    if (this.categoryProbabilities == null) {
      sb.append("null");
    } else {
      sb.append(this.categoryProbabilities);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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

  private static class TDatumNominalResultsStandardSchemeFactory implements SchemeFactory {
    public TDatumNominalResultsStandardScheme getScheme() {
      return new TDatumNominalResultsStandardScheme();
    }
  }

  private static class TDatumNominalResultsStandardScheme extends StandardScheme<TDatumNominalResults> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TDatumNominalResults struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CATEGORY_PROBABILITIES
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map24 = iprot.readMapBegin();
                struct.categoryProbabilities = new HashMap<String,Double>(2*_map24.size);
                for (int _i25 = 0; _i25 < _map24.size; ++_i25)
                {
                  String _key26; // required
                  double _val27; // required
                  _key26 = iprot.readString();
                  _val27 = iprot.readDouble();
                  struct.categoryProbabilities.put(_key26, _val27);
                }
                iprot.readMapEnd();
              }
              struct.setCategoryProbabilitiesIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TDatumNominalResults struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.categoryProbabilities != null) {
        oprot.writeFieldBegin(CATEGORY_PROBABILITIES_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.DOUBLE, struct.categoryProbabilities.size()));
          for (Map.Entry<String, Double> _iter28 : struct.categoryProbabilities.entrySet())
          {
            oprot.writeString(_iter28.getKey());
            oprot.writeDouble(_iter28.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TDatumNominalResultsTupleSchemeFactory implements SchemeFactory {
    public TDatumNominalResultsTupleScheme getScheme() {
      return new TDatumNominalResultsTupleScheme();
    }
  }

  private static class TDatumNominalResultsTupleScheme extends TupleScheme<TDatumNominalResults> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TDatumNominalResults struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetCategoryProbabilities()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetCategoryProbabilities()) {
        {
          oprot.writeI32(struct.categoryProbabilities.size());
          for (Map.Entry<String, Double> _iter29 : struct.categoryProbabilities.entrySet())
          {
            oprot.writeString(_iter29.getKey());
            oprot.writeDouble(_iter29.getValue());
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TDatumNominalResults struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TMap _map30 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
          struct.categoryProbabilities = new HashMap<String,Double>(2*_map30.size);
          for (int _i31 = 0; _i31 < _map30.size; ++_i31)
          {
            String _key32; // required
            double _val33; // required
            _key32 = iprot.readString();
            _val33 = iprot.readDouble();
            struct.categoryProbabilities.put(_key32, _val33);
          }
        }
        struct.setCategoryProbabilitiesIsSet(true);
      }
    }
  }

}
