package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.JSONObject;

import fasta.FastaBean;
import fasta.FastaBean.DetailedType;
import fasta.FastaBean.Type;

public class ReferenceConfigReader {

  public FastaBean read(File file) throws Exception {
    InputStream inputStream = new FileInputStream(file);
    byte[] buffer = new byte[inputStream.available()];
    while (inputStream.read(buffer) != -1);

    String jsonText = new String(buffer);
    JSONObject jsonObject = new JSONObject(jsonText);
    inputStream.close();

    JSONObject reference = jsonObject.getJSONObject("reference");
    String referenceName = reference.getString("name");
    String referenceDescription = reference.getString("description");
    String referenceVersion = reference.getString("version");
    Type referenceType = FastaBean.Type.valueOf(reference.getString("type"));
    String referenceSpecies = reference.getString("species");
    String referencePath = reference.getString("path");
    String referenceDate = reference.getString("date");
    DetailedType referenceDetailedType =
        FastaBean.DetailedType.valueOf(reference.getString("detailedType"));

    FastaBean newReference = new FastaBean();
    newReference.setName(referenceName);
    newReference.setDescription(referenceDescription);
    newReference.setVersion(referenceVersion);
    newReference.setOmicsType(referenceType);
    newReference.setDetailedType(referenceDetailedType);
    newReference.setSpecies(referenceSpecies);
    newReference.setPath(referencePath);
    newReference.setDate(referenceDate);

    return newReference;
  }
}

