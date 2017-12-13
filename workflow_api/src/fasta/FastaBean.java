package fasta;

import java.io.Serializable;

public class FastaBean implements Serializable {
  public enum DetailedType {
    WholeGenomeFasta("Whole Genome Fasta"), BowtieIndex("Bowtie Index"), BWAIndex(
        "BWA Index"), shRNALibrary(
            "shRNA Library"), shRNABarcodes("shRNA Barcode"), Proteome("Proteome");

    private DetailedType(String name) {
      this.name = name;
    }

    private final String name;

    public String toString() {
      return name;
    }
  };


  public enum Type {
    NGS, Misc, Proteomics, Transcriptomics, Metabolomics
  };

  /**
   * 
   */
  private static final long serialVersionUID = 6711508752415367451L;

  private String description;
  private String name;
  private String version;
  private String species;
  private String path;
  private Type omicsType;
  private DetailedType detailedType;
  private String date;

  public FastaBean(String name, String description, String version, String species, String path,
      Type omicsType) {
    super();
    this.description = description;
    this.name = name;
    this.version = version;
    this.species = species;
    this.path = path;
    this.omicsType = omicsType;
  }

  public FastaBean() {
    super();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Type getOmicsType() {
    return omicsType;
  }

  public void setOmicsType(Type omicsType) {
    this.omicsType = omicsType;
  }

  public DetailedType getDetailedType() {
    return detailedType;
  }

  public void setDetailedType(DetailedType detailedType) {
    this.detailedType = detailedType;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "FastaBean [name=" + name + ", version=" + version + ", " + "species=" + species
        + ", path=" + path + "type= " + omicsType + " detailedType= " + detailedType + "]";
  }
}
