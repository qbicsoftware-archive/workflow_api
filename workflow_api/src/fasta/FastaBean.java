package fasta;

import java.io.Serializable;

public class FastaBean implements Serializable {
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
  private Type type;


  public FastaBean(String name, String description, String version, String species, String path,
      Type type) {
    super();
    this.description = description;
    this.name = name;
    this.version = version;
    this.species = species;
    this.path = path;
    this.type = type;
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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "FastaBean [name=" + name + ", version=" + version + ", " + "species=" + species
        + ", path=" + path + "type= " + type + "]";
  }
}
