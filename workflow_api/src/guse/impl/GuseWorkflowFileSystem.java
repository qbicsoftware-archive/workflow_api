package guse.impl;

import guse.workflowrepresentation.GuseNode;
import guse.workflowrepresentation.GuseWorkflowRepresentation;
import guse.workflowrepresentation.InputPort;
import guse.workflowrepresentation.InputPort.Type;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import logging.Log4j2Logger;
import logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import submitter.Workflow;
import submitter.parameters.FloatParameter;
import submitter.parameters.IntParameter;
import submitter.parameters.Parameter;
import submitter.parameters.StringParameter;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.config.reader.InvalidCTDFileException;
import com.genericworkflownodes.knime.config.writer.CTDConfigurationWriter;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;

public class GuseWorkflowFileSystem {

  private static Logger LOGGER = new Log4j2Logger(GuseWorkflowFileSystem.class);

  private final static int DEFAULT_BUFFER_SIZE = 32768;
  private File pathToMainWorkflowFolder;

  private File certificate;

  private Map<String, String> dropBoxPaths;

  public GuseWorkflowFileSystem(File pathToMainWorkflowFolder, File certificate, File dropBoxPaths)
      throws Exception {
    this.pathToMainWorkflowFolder = pathToMainWorkflowFolder;
    this.certificate = certificate;
    this.dropBoxPaths = parseDropBoxPaths(dropBoxPaths);
  }

  public File getPathToMainWorkflowFolder() {
    return pathToMainWorkflowFolder;
  }

  public void setPathToMainWorkflowFolder(File pathToMainWorkflowFolder) {
    this.pathToMainWorkflowFolder = pathToMainWorkflowFolder;
  }

  public void setCertificate(File certificate) {
    this.certificate = certificate;
  }

  /**
   * Returns true if the path exists. With Paths.get(String parent, String child1, String child2),
   * Strings can be concatenated to a Path.
   * 
   * @param target
   * @return true if the path exists on the file system.
   */
  public boolean pathExists(final Path target) {
    return Files.exists(target);
  }

  /**
   * Helper method. copies files and directories from source to target source and target can be
   * created via the command: FileSystems.getDefault().getPath(String path, ... String more) or
   * Paths.get(String path, ... String more) Note: overrides existing folders
   * 
   * @param source
   * @param target
   * @return true if copying was successful
   */
  public boolean copy(final Path source, final Path target) {
    try {
      Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
          new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              Path targetdir = target.resolve(source.relativize(dir));
              try {
                Files.copy(dir, targetdir, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
              } catch (FileAlreadyExistsException e) {
                if (!Files.isDirectory(targetdir)) {
                  throw e;
                }
              }

              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.copy(file, target.resolve(source.relativize(file)));
              return FileVisitResult.CONTINUE;

            }

          });
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Helper method. deletes Path start and all files and subdirectories inside it, if it is a
   * directory start can be created via the command: FileSystems.getDefault().getPath(String path,
   * ... String more) or Paths.get(String path, ... String more)
   * 
   * @param start
   * @return true if deletion was successful
   */
  public boolean delete(Path start) {
    try {
      Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
          if (e == null) {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          } else {
            // directory iteration failed
            throw e;
          }
        }
      });
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    while (true) {
      int readCount = in.read(buffer);
      if (readCount < 0) {
        break;
      }
      out.write(buffer, 0, readCount);
    }
  }

  public void copy(File file, OutputStream out) throws IOException {
    InputStream in = new FileInputStream(file);
    try {
      copy(in, out);
    } finally {
      in.close();
    }
  }

  public void copy(InputStream in, File file) throws IOException {
    OutputStream out = new FileOutputStream(file);
    try {
      copy(in, out);
    } finally {
      out.close();
    }
  }

  /**
   * Helper method to zip a directory into a zip file. No checking is performed, whether the given
   * {#link java.io.File} object is really a directory.
   * 
   * @param directory
   * @param zipfile
   * @throws IOException
   */
  public void zip(File directory, File zipfile) throws IOException {
    URI base = directory.toURI();
    Deque<File> queue = new LinkedList<File>();
    queue.push(directory);
    OutputStream out = new FileOutputStream(zipfile);
    Closeable res = null;
    try {
      ZipOutputStream zout = new ZipOutputStream(out);
      res = zout;
      while (!queue.isEmpty()) {
        directory = queue.pop();
        for (File kid : directory.listFiles()) {
          String name = base.relativize(kid.toURI()).getPath();
          if (kid.isDirectory()) {
            queue.push(kid);
            name = name.endsWith("/") ? name : name + "/";
            zout.putNextEntry(new ZipEntry(name));
          } else {
            zout.putNextEntry(new ZipEntry(name));
            copy(kid, zout);
            zout.closeEntry();
          }
        }
      }
    } finally {
      if (res != null) {
        res.close();
      }

    }
  }

  /**
   * creates a random temporary directory for the given workflowPath. And copies the original
   * workflow directory to the temp one.
   * 
   * @param extension
   * @return returns the created File name. Be aware that this file does not exists.
   */
  public File generateTempDirectory(File workflowPath) {
    File tmp = null;
    try {
      tmp = File.createTempFile(workflowPath.getName(), "tmp", null);
      tmp.deleteOnExit();
      copy(Paths.get(workflowPath.toURI()), Paths.get(tmp.toURI()));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return tmp;
  }

  /**
   * creates a random temporary file name with extension as file ending
   * 
   * @param extension
   * @return returns the created File name. Be aware that this file does not exists.
   */
  public File generateTempFile(String workflow, String extension) {
    File tmp = null;
    try {
      tmp = File.createTempFile(workflow, extension, null);
      tmp.deleteOnExit();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return tmp;
  }

  /**
   * checks, whether the folder in which the workflows are supposed be, does exist and whether the
   * certificate provided does exist. Note: It does not check whether there are really any workflows
   * in the folder
   * 
   * @return
   */
  public boolean isAvailable() {
    boolean workflowFolderExists =
        (!(this.pathToMainWorkflowFolder == null || !this.pathToMainWorkflowFolder.exists() || this.pathToMainWorkflowFolder
            .isFile()));
    boolean certificateExists =
        certificate != null && certificate.exists() && certificate.isFile()
            && certificate.getName().endsWith("zip");

    return workflowFolderExists && certificateExists;
  }

  /**
   * Get ALL workflows...ignore or implement what's below... (Get workflows that fullfill the filter
   * criterium. This function checks the pathToMainWorkflowFolder and filters all directories with
   * the filter as name. e.g. filter=mzml Folder structure of pathToMainWorkflowFolder workflows |
   * --mzml | --workflow1 --workflow2 | --raw | --workflow3 --workflow4 In that case only workflow1,
   * workflow2 will be returned.)
   * 
   * @return
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public Set<Workflow> getWorkflows() throws IllegalArgumentException, IOException {
    Set<Workflow> workflows = new HashSet<Workflow>();
    File[] guseWorkflows = this.pathToMainWorkflowFolder.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        // exclude hidden files and folders
        return !(dir.getName().startsWith(".") || name.startsWith("."));
      }
    });
    assert guseWorkflows.length > 0;
    LOGGER.debug("workflow files found: " + guseWorkflows);
    for (File file : guseWorkflows) {
      try {
        workflows.add(getWorkflow(file));
      } catch (JAXBException | FileNotFoundException | InvalidCTDFileException e) {
        throw new IllegalArgumentException(e.getMessage() + ", file: " + file.getAbsolutePath());
      }
    }
    return workflows;
  }

  /**
   * returns an instance of {@link submitter.Workflow}. for the given guse workflow folder
   * 
   * @param guseWorkflow a File object with path to a guse conform workflow directory, that contains
   *        the workflow.xml file.
   * @return returns an object that implements the {@link submitter.Workflow}.
   * @throws IllegalArgumentException
   * @throws InvalidCTDFileException
   * @throws IOException
   */
  Workflow getWorkflow(File guseWorkflow) throws JAXBException, IllegalArgumentException,
      InvalidCTDFileException, IOException {
    File workflowXml = Paths.get(guseWorkflow.getAbsolutePath(), "workflow.xml").toFile();
    de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow workflowXmlBean = getWorkflowXml(workflowXml);

    List<Object> graphOrReal = workflowXmlBean.getGrafOrReal();
    Iterator<Object> it = graphOrReal.iterator();

    de.uni_tuebingen.qbic.guseWorkflowBeans.Real real = null;

    while (it.hasNext()) {
      Object o = it.next();
      if (o instanceof de.uni_tuebingen.qbic.guseWorkflowBeans.Real) {
        real = (de.uni_tuebingen.qbic.guseWorkflowBeans.Real) o;
      }
    }

    if (real == null)
      throw new IllegalArgumentException("Could not retrieve real from guse workflow xml file for "
          + guseWorkflow.getAbsolutePath());
    Map<String, GuseNode> nodes = new HashMap<String, GuseNode>();
    for (de.uni_tuebingen.qbic.guseWorkflowBeans.Job job : real.getJob()) {
      GuseNode node = new GuseNode(job.getName(), job.getText(), null);
      Map<String, InputPort> inputPorts = new HashMap<String, InputPort>();
      for (de.uni_tuebingen.qbic.guseWorkflowBeans.Input input : job.getInput()) {
        // ignore inputs that are explicitly handled by guse
        if (!input.getPrejob().isEmpty())
          continue;

        InputPort port = new InputPort();
        port.setName(input.getName());
        port.setDescription(input.getText());
        port.setPortNumber(input.getSeq().intValue());
        // GET PARAMETERS
        // gUSE parameter File(s) or input port file(s) are always 0 and in the folder 'inputs'.
        File parameterFile =
            Paths.get(guseWorkflow.getAbsolutePath(), workflowXmlBean.getName(), job.getName(),
                "inputs", String.valueOf(input.getSeq().intValue()), "0").toFile();
        if (input.getName().contains("FILESTOSTAGE")) {
          Map<String, Parameter> params = null;
          try {
            params = parseParamsCtdFilesToStage(parameterFile);
          } catch (InvalidCTDFileException e) {
            params = parseTabSeperatedFileToStage(parameterFile);
          }
          port.setParams(params);
          port.setType(Type.FILESTOSTAGE);
        } else if (input.getName().contains("JOBNAME")) {
          port.setParams(parseJobName(parameterFile));
          port.setType(Type.JOBNAME);
        } else if (input.getName().contains("ZIP")) {
          port.setParams(parseZippedCtds(parameterFile));
          port.setType(Type.CTD_ZIP);
        } else if (input.getName().contains("CTD")) {
          port.setParams(parseParamsCtd(parameterFile));
          port.setType(Type.CTD);
        } else if (input.getName().contains("REGISTERNAME")) {
          port.setParams(parseRegisterName(parameterFile));
          port.setType(Type.REGISTERNAME);
        } else if (input.getName().contains("PHENOFILE")) {
          port.setType(Type.PHENOFILE);
        }
        // TODO should users get any special treatment?
        else if (input.getName().contains("USER")) {
          port.setParams(parseUser(parameterFile));
          port.setType(Type.USER);
        } else if (input.getName().contains("DROPBOX")) {
          Map<String, Parameter> p = new HashMap<String, Parameter>();
          StringParameter dummyValue =
              new StringParameter("dropbox", "write results to this dropbox", true, true,
                  new ArrayList<String>());
          dummyValue.setValue("/tmp");
          p.put("dropbox", dummyValue);
          port.setParams(p);
          port.setType(Type.DROPBOX);
        } else {
          throw new IllegalArgumentException("Input Port " + input.getName() + " of node "
              + job.getName() + " in workflow " + workflowXmlBean.getName() + " in "
              + guseWorkflow.getAbsolutePath()
              + " does not contain one of the required name substrings : " + Type.values());
        }
        inputPorts.put(port.getName(), port);
      }
      // GET cmd line parameters and submitter parameters
      for (de.uni_tuebingen.qbic.guseWorkflowBeans.Execute execute : job.getExecute()) {
        if (execute.getKey().equals("gridtype")) {
          node.setGridType(execute.getValue());
        } else if (execute.getKey().equals("grid")) {
          node.setGrid(execute.getValue());
        } else if (execute.getKey().equals("resource")) {
          node.setResource(execute.getValue());
        } else if (execute.getKey().equals("params")) {
          ArrayList<String> tmp = new ArrayList<String>();
          String[] split = execute.getValue().split(" ");
          for (String s : split) {
            tmp.add(s);
          }
          node.setCmdParams(tmp);
        }
      }
      // GET moab directives
      ArrayList<String> moabKeydirectives = new ArrayList<String>();
      for (de.uni_tuebingen.qbic.guseWorkflowBeans.Description description : job.getDescription()) {
        if (description.getKey().equals("moab.keydirectives")) {
          String[] split = description.getValue().split(" ");
          for (String s : split) {
            moabKeydirectives.add(s);
          }
        }
      }
      node.setMoabKeydirectives(moabKeydirectives);
      node.setInputPorts(inputPorts);
      nodes.put(node.getTitle(), node);
    }

    GuseWorkflowRepresentation guseWorkflowRep =
        new GuseWorkflowRepresentation(real.getName(), "", real.getText(), "no version available",
            null, null, "unknown dataset type", "unknown experiment type");
    guseWorkflowRep.setDirectory(guseWorkflow);
    guseWorkflowRep.setWorkflowXml(workflowXmlBean);

    guseWorkflowRep.setNodes(nodes);
    return guseWorkflowRep;

  }

  Map<String, Parameter> parseZippedCtds(File parameterFile) throws IOException,
      InvalidCTDFileException {
    ZipFile file = new ZipFile(parameterFile);
    Map<String, Parameter> params = new LinkedHashMap<String, Parameter>();
    try {
      Enumeration<? extends ZipEntry> en = file.entries();
      while (en.hasMoreElements()) {
        ZipEntry entry = en.nextElement();
        if (entry.getName().endsWith("ini") || entry.getName().endsWith("ctd")) {
          InputStream stream = file.getInputStream(entry);
          params.putAll(parseParamsCtd(stream));
        }
      }
    } finally {
      file.close();
    }
    return params;
  }

  /**
   * writes a zipped folder which has as content ctds. Note right now the folder is called inis and
   * files end with ini TODO make that optional
   * 
   * @param portFile
   * @param input
   * @throws InvalidCTDFileException
   * @throws IOException
   */
  void writeZippedCtd(File portFile, InputPort input) throws InvalidCTDFileException, IOException {
    String inisFolderName = "inis";
    ZipFile file = new ZipFile(portFile);
    File parent = portFile.getParentFile();
    File inis = Paths.get(parent.getAbsolutePath(), "temp").toFile();
    File realinis = Paths.get(inis.getAbsolutePath(), inisFolderName).toFile();

    try {
      if (!inis.mkdir() || !realinis.mkdir()) {
        throw new IOException("Could not write zipped ctd file: could not create inis directory: "
            + realinis.getAbsolutePath());
      }
      Enumeration<? extends ZipEntry> en = file.entries();
      while (en.hasMoreElements()) {
        ZipEntry entry = en.nextElement();
        if (!entry.isDirectory()
            && (entry.getName().endsWith("ini") || entry.getName().endsWith("ctd"))) {
          InputStream stream = file.getInputStream(entry);
          INodeConfiguration inode = getKnimeNodeConfiguration(stream);
          for (Entry<String, Parameter> inputentry : input.getParams().entrySet()) {
            com.genericworkflownodes.knime.parameter.Parameter<?> param =
                inode.getParameter(inputentry.getKey());
            writeParameter(param, inputentry.getValue());
          }
          File config = Paths.get(inis.getAbsolutePath(), entry.getName()).toFile();
          CTDConfigurationWriter writer = new CTDConfigurationWriter(config);
          writer.write(inode);
        }
      }
    } finally {
      file.close();
    }
    if (!portFile.delete())
      new IOException("Could not write zipped ctd file; could not delete old zip file: "
          + portFile.getAbsolutePath());
    zip(inis, portFile);
    // delete(inis.toPath());
  }

  /**
   * 
   * @param parameterFile
   * @return
   * @throws FileNotFoundException
   * @throws IllegalArgumentException
   */
  Map<String, Parameter> parseTabSeperatedFileToStage(File parameterFile)
      throws FileNotFoundException, IllegalArgumentException {
    Map<String, Parameter> param = new HashMap<String, Parameter>();
    try (Scanner scanner = new Scanner(parameterFile)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] keyValue = line.split("\t");
        if (keyValue.length != 2)
          throw new IllegalArgumentException(
              "parameter File "
                  + parameterFile.getAbsolutePath()
                  + " is corrupt. Line "
                  + line
                  + " . If you use tab seprated files for files to stage instead of ctds it should have the following structure: key\\tpath");

        String[] extensions = keyValue[1].split(".");

        String ext = "";
        if (extensions.length > 2)
          ext = extensions[extensions.length - 1];
        ArrayList<String> range = new ArrayList<String>();
        if (!ext.isEmpty())

          range.add(ext);

        submitter.parameters.FileParameter tmp =
            new submitter.parameters.FileParameter(keyValue[0], "File to stage", false, true,
                keyValue[1], range);
        param.put(keyValue[0], tmp);
      }
    }
    return param;
  }

  Map<String, Parameter> parseRegisterName(File parameterFile) throws FileNotFoundException,
      IllegalArgumentException {
    Map<String, Parameter> param = new HashMap<String, Parameter>();
    try (Scanner scanner = new Scanner(parameterFile)) {
      String line = scanner.nextLine();
      String[] isOk = line.split("-");
      if (isOk.length != 3)
        throw new IllegalArgumentException("parameter File " + parameterFile.getAbsolutePath()
            + " is corrupt. Line " + line
            + " .Should have the following structure: space-project-experiment");
      StringParameter tmp =
          new StringParameter("", "Prefix used to register the results of this workflow.", false,
              true, new ArrayList<String>());
      tmp.setValue(line);
      param.put("", tmp);
    }
    return param;
  }

  Map<String, Parameter> parseUser(File parameterFile) throws FileNotFoundException,
      IllegalArgumentException {
    Map<String, Parameter> param = new HashMap<String, Parameter>();
    try (Scanner scanner = new Scanner(parameterFile)) {
      String line = scanner.nextLine();
      StringParameter tmp =
          new StringParameter("", "User for whome the workflow will be executed.", false, true,
              new ArrayList<String>());
      tmp.setValue(line);
      param.put("", tmp);
    }
    return param;
  }


  Map<String, Parameter> parseJobName(File parameterFile) throws FileNotFoundException,
      IllegalArgumentException {
    Map<String, Parameter> param = new HashMap<String, Parameter>();
    try (Scanner scanner = new Scanner(parameterFile)) {
      String line = scanner.nextLine();
      StringParameter tmp =
          new StringParameter("", "workflow job name", false, true, new ArrayList<String>());
      tmp.setValue(line);
      param.put("", tmp);
    }
    return param;
  }


  Map<String, Parameter> parseParamsCtdFilesToStage(File ctd) throws FileNotFoundException,
      InvalidCTDFileException {
    Map<String, Parameter> params = new HashMap<String, Parameter>();
    INodeConfiguration inode = getKnimeNodeConfiguration(ctd);
    for (String key : inode.getParameterKeys()) {
      com.genericworkflownodes.knime.parameter.Parameter<?> param = inode.getParameter(key);
      if (param instanceof com.genericworkflownodes.knime.parameter.FileParameter) {
        com.genericworkflownodes.knime.parameter.FileParameter fp =
            (com.genericworkflownodes.knime.parameter.FileParameter) param;
        params.put(key, knimeToQbicFileParameter(key, fp));
        // string with range
      } else if (param instanceof com.genericworkflownodes.knime.parameter.FileListParameter) {
        com.genericworkflownodes.knime.parameter.FileListParameter fp =
            (com.genericworkflownodes.knime.parameter.FileListParameter) param;
        params.put(key, knimeToQbicFileListParameter(key, fp));
      }
    }
    return params;
  }


  Map<String, Parameter> parseParamsCtd(InputStream ctd) throws InvalidCTDFileException,
      FileNotFoundException {

    Map<String, Parameter> params = new HashMap<String, Parameter>();
    INodeConfiguration inode = getKnimeNodeConfiguration(ctd);
    for (String key : inode.getParameterKeys()) {
      com.genericworkflownodes.knime.parameter.Parameter<?> param = inode.getParameter(key);
      // integer
      if (param instanceof IntegerParameter) {
        IntegerParameter ip = (IntegerParameter) param;
        IntParameter gip =
            new IntParameter(key, ip.getDescription(), ip.isAdvanced(), !ip.isOptional(),
                ip.getLowerBound(), ip.getUpperBound());
        gip.setValue(ip.getValue());
        params.put(key, gip);
        // double
      } else if (param instanceof DoubleParameter) {
        DoubleParameter dp = (DoubleParameter) param;
        float lowerBound = -Float.MAX_VALUE;
        float upperBound = Float.MAX_VALUE;
        if (dp.getUpperBound() != Double.POSITIVE_INFINITY) {
          upperBound = dp.getUpperBound().floatValue();
        }
        if (dp.getLowerBound() != Double.NEGATIVE_INFINITY) {
          lowerBound = dp.getLowerBound().floatValue();
        }
        float value = 0f;
        if (dp.getValue() != Double.NEGATIVE_INFINITY && dp.getValue() != Double.POSITIVE_INFINITY) {
          value = dp.getValue().floatValue();
        }

        FloatParameter gip =
            new FloatParameter(key, dp.getDescription(), dp.isAdvanced(), !dp.isOptional(),
                lowerBound, upperBound);
        gip.setValue(value);
        params.put(key, gip);
        // file
      } else if (param instanceof com.genericworkflownodes.knime.parameter.FileParameter) {
        com.genericworkflownodes.knime.parameter.FileParameter fp =
            (com.genericworkflownodes.knime.parameter.FileParameter) param;
        params.put(key, knimeToQbicFileParameter(key, fp));
        // string with range
      } else if (param instanceof com.genericworkflownodes.knime.parameter.FileListParameter) {
        com.genericworkflownodes.knime.parameter.FileListParameter fp =
            (com.genericworkflownodes.knime.parameter.FileListParameter) param;
        params.put(key, knimeToQbicFileListParameter(key, fp));
        // string with range
      } else if (param instanceof StringChoiceParameter) {
        StringChoiceParameter scp = (StringChoiceParameter) param;
        boolean isRequired = !scp.isOptional();
        /*
         * From StringChoiceParamter Documentation: If the set itself is optional, the empty string
         * is via convention also a valid choice. It means that scp.getLabels returns the labels +
         * an empty String
         */
        scp.setIsOptional(false);
        StringParameter gip =
            new StringParameter(key, scp.getDescription(), scp.isAdvanced(), isRequired,
                scp.getLabels());
        gip.setValue(scp.getValue());
        params.put(key, gip);
      } else if (param instanceof com.genericworkflownodes.knime.parameter.StringParameter) {
        com.genericworkflownodes.knime.parameter.StringParameter scp =
            (com.genericworkflownodes.knime.parameter.StringParameter) param;
        StringParameter gip =
            new StringParameter(key, scp.getDescription(), scp.isAdvanced(), !scp.isOptional(),
                new ArrayList<String>());

        if (scp.getValue() == null)
          gip.setValue("");
        else
          gip.setValue(scp.getValue());
        params.put(key, gip);
      } else if (param instanceof BoolParameter) {
        com.genericworkflownodes.knime.parameter.BoolParameter scp =
            (com.genericworkflownodes.knime.parameter.BoolParameter) param;
        ArrayList<String> range = new ArrayList<String>();
        range.add("true");
        range.add("false");
        StringParameter gip =
            new StringParameter(key, scp.getDescription(), scp.isAdvanced(), !scp.isOptional(),
                range);
        gip.setValue(String.valueOf(scp.getValue()));
        params.put(key, gip);
      }
    }
    return params;
  }

  Map<String, Parameter> parseParamsCtd(File ctd) throws FileNotFoundException,
      InvalidCTDFileException {
    return parseParamsCtd(new FileInputStream(ctd));
  }

  /**
   * Converts a knime file Parameter to a qbic file parameter
   * 
   * @param title
   * @param fp
   * @return
   */
  Parameter knimeToQbicFileParameter(String title,
      com.genericworkflownodes.knime.parameter.FileParameter fp) {
    String value = "";
    if (fp.getValue() != null)
      value = fp.getValue();

    submitter.parameters.FileParameter gip =
        new submitter.parameters.FileParameter(title, fp.getDescription(), fp.isAdvanced(),
            !fp.isOptional(), value, fp.getPort().getMimeTypes());

    return gip;

  }

  /**
   * Converts a knime file list Parameter to a qbic file list parameter
   * 
   * @param title
   * @param fp
   * @return
   */
  Parameter knimeToQbicFileListParameter(String title,
      com.genericworkflownodes.knime.parameter.FileListParameter fp) {
    List<String> value = null;

    if (fp.getValue() != null)
      value = fp.getValue();
    else
      value = new ArrayList<String>();

    submitter.parameters.FileListParameter gip =
        new submitter.parameters.FileListParameter(title, fp.getDescription(), fp.isAdvanced(),
            !fp.isOptional(), value, fp.getPort().getMimeTypes());
    return gip;
  }

  /**
   * returns a {@link de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow} instance.
   * 
   * @param guseWorkflow a File object with path to a guse conform workflow directory, that contains
   *        the workflow.xml file.
   * @return de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow which is a bean representation of the
   *         workflow.zml
   * @throws JAXBException
   */
  private de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow getWorkflowXml(File file)
      throws JAXBException {
    JAXBContext jaxbContext;
    jaxbContext = JAXBContext.newInstance("de.uni_tuebingen.qbic.guseWorkflowBeans");
    javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    StreamSource source = new StreamSource(file);
    JAXBElement<de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow> customerElement =
        unmarshaller.unmarshal(source, de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow.class);
    return (de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow) customerElement.getValue();
  }

  public File GuseWorkflowRepresentationToGuseConverter(GuseWorkflowRepresentation workflow,
      String foreignID, String user) throws IllegalArgumentException, IOException,
      InvalidCTDFileException {
    if (workflow == null)
      throw new IllegalArgumentException("workflow is null");
    if (foreignID == null || user == null || foreignID.isEmpty() || user.isEmpty()) {
      throw new IllegalArgumentException("can not convert " + workflow.getID() + " with foreignID "
          + foreignID + " for user " + user);
    }
    File workflowDir = workflow.getDirectory();
    String uniqueWorkflowId = workflow.getID();
    uniqueWorkflowId += System.currentTimeMillis();

    File tmpWorkflowDir = generateTempDirectory(workflowDir);
    String tmpWorkflowInputDir =
        Paths.get(tmpWorkflowDir.getAbsolutePath(), workflow.getID()).toString();

    List<GuseNode> nodes = workflow.getGuseNodes();
    for (GuseNode node : nodes) {
      for (Entry<String, InputPort> entry : node.getInputPorts().entrySet()) {
        InputPort input = entry.getValue();
        File portFile =
            Paths.get(tmpWorkflowInputDir, node.getTitle(), "inputs",
                String.valueOf(input.getPortNumber()), "0").toFile();
        if (input.getType() == Type.CTD) {
          writeCtd(portFile, input);
        } else if (input.getType() == Type.FILESTOSTAGE) {
          // try to write ctd and use fall back tsv if it does not work
          try {
            writeCtdFilesToStage(portFile, input);
          } catch (InvalidCTDFileException e) {
            writeTabSeperatedFileToStage(portFile, input);
          }
        } else if (input.getType() == Type.JOBNAME) {
          writeJobName(portFile, uniqueWorkflowId);
        } else if (input.getType() == Type.REGISTERNAME) {
          writeRegisterName(portFile, foreignID);
          // phenofile is a file containing a mapping between file names and experiment variables
          // used in microarray qc workflow
        } else if (input.getType() == Type.PHENOFILE) {
          writePheno(portFile, (String) input.getParams().get("pheno").getValue());
        } else if (input.getType() == Type.USER) {
          // TODO write user back
        } else if (input.getType() == Type.CTD_ZIP) {
          writeZippedCtd(portFile, input);
        } else if (input.getType() == Type.DROPBOX) {
          if (dropBoxPaths.containsKey(workflow.getSampleType())) {
            writeDropBoxPath(portFile, dropBoxPaths.get(workflow.getSampleType()));
          } else {
            throw new IllegalArgumentException(
                String
                    .format(
                        "dropbox path not known for sample %s.\nDo you have a mapping for this sample type in dropbox.json?",
                        workflow.getSampleType()));
          }

        }
      }
    }

    File zipfile = new File(tmpWorkflowDir.toString() + ".zip");
    zip(tmpWorkflowDir, zipfile);

    return zipfile;
  }

  private void writeCtdFilesToStage(File portFile, InputPort input) throws InvalidCTDFileException,
      IOException {
    INodeConfiguration inode = getKnimeNodeConfiguration(portFile);
    for (Entry<String, Parameter> entry : input.getParams().entrySet()) {
      com.genericworkflownodes.knime.parameter.Parameter<?> param =
          inode.getParameter(entry.getKey());
      if (param instanceof com.genericworkflownodes.knime.parameter.FileParameter) {
        com.genericworkflownodes.knime.parameter.FileParameter fp =
            (com.genericworkflownodes.knime.parameter.FileParameter) param;
        fp.setValue((String) entry.getValue().getValue());
        // string with range
      } else if (param instanceof com.genericworkflownodes.knime.parameter.FileListParameter) {
        com.genericworkflownodes.knime.parameter.FileListParameter fp =
            (com.genericworkflownodes.knime.parameter.FileListParameter) param;
        fp.setValue((List<String>) entry.getValue().getValue());
        // string with range
      }
      CTDConfigurationWriter writer = new CTDConfigurationWriter(portFile);
      writer.write(inode);
    }
  }

  void writeRegisterName(File portFile, String foreignID) throws IOException {
    FileWriter fileWriter = new FileWriter(portFile);
    fileWriter.write(foreignID);
    fileWriter.close();
  }

  void writePheno(File portFile, String text) throws IOException {
    FileWriter fileWriter = new FileWriter(portFile);
    fileWriter.write(text);
    fileWriter.close();
  }

  void writeJobName(File portFile, String uniqueWorkflowId) throws IOException {
    FileWriter fileWriter = new FileWriter(portFile);
    fileWriter.write(uniqueWorkflowId);
    fileWriter.close();
  }

  void writeDropBoxPath(File portFile, String dropBoxPath) throws IOException {
    FileWriter fileWriter = new FileWriter(portFile);
    fileWriter.write(dropBoxPath);
    fileWriter.close();
  }

  void writeTabSeperatedFileToStage(File portFile, InputPort input) throws IOException {
    FileWriter fileWriter = new FileWriter(portFile);
    for (Entry<String, Parameter> entry : input.getParams().entrySet()) {
      fileWriter
          .write(entry.getKey() + "\t" + entry.getValue().getValue() + System.lineSeparator());
    }

    fileWriter.close();
  }

  void writeCtd(File portFile, InputPort input) throws IOException, InvalidCTDFileException {
    INodeConfiguration inode = getKnimeNodeConfiguration(portFile);
    for (Entry<String, Parameter> entry : input.getParams().entrySet()) {
      com.genericworkflownodes.knime.parameter.Parameter<?> param =
          inode.getParameter(entry.getKey());

      writeParameter(param, entry.getValue());
    }
    CTDConfigurationWriter writer = new CTDConfigurationWriter(portFile);
    writer.write(inode);
  }

  INodeConfiguration getKnimeNodeConfiguration(File portFile) throws FileNotFoundException,
      InvalidCTDFileException {
    CTDConfigurationReader reader = new CTDConfigurationReader();
    return reader.read(new FileInputStream(portFile));
  }

  INodeConfiguration getKnimeNodeConfiguration(InputStream portStream)
      throws FileNotFoundException, InvalidCTDFileException {
    CTDConfigurationReader reader = new CTDConfigurationReader();
    return reader.read(portStream);
  }

  /**
   * writes the value of newParam to param, based on its class types
   * 
   * @param param
   * @param newParam
   */
  public void writeParameter(com.genericworkflownodes.knime.parameter.Parameter<?> param,
      Parameter newParam) {
    // integer
    if (param instanceof IntegerParameter) {
      IntegerParameter ip = (IntegerParameter) param;

      ip.setValue((Integer) newParam.getValue());
      // double
    } else if (param instanceof DoubleParameter) {
      DoubleParameter dp = (DoubleParameter) param;
      Float fl = (Float) newParam.getValue();
      dp.setValue(fl.doubleValue());
      // file
    } else if (param instanceof com.genericworkflownodes.knime.parameter.FileParameter) {
      com.genericworkflownodes.knime.parameter.FileParameter fp =
          (com.genericworkflownodes.knime.parameter.FileParameter) param;
      fp.setValue((String) newParam.getValue());
      // string with range
    } else if (param instanceof com.genericworkflownodes.knime.parameter.FileListParameter) {
      com.genericworkflownodes.knime.parameter.FileListParameter fp =
          (com.genericworkflownodes.knime.parameter.FileListParameter) param;
      fp.setValue((List<String>) newParam.getValue());
      // string with range
    } else if (param instanceof StringChoiceParameter) {
      StringChoiceParameter scp = (StringChoiceParameter) param;
      scp.setValue((String) newParam.getValue());
    } else if (param instanceof com.genericworkflownodes.knime.parameter.StringParameter) {
      com.genericworkflownodes.knime.parameter.StringParameter scp =
          (com.genericworkflownodes.knime.parameter.StringParameter) param;
      scp.setValue((String) newParam.getValue());
    } else if (param instanceof BoolParameter) {
      com.genericworkflownodes.knime.parameter.BoolParameter scp =
          (com.genericworkflownodes.knime.parameter.BoolParameter) param;
      Boolean bo = new Boolean((String) newParam.getValue());
      scp.setValue(bo);
    }
  }

  private Map<String, String> parseDropBoxPaths(File file) throws Exception {
    InputStream inputStream = new FileInputStream(file);
    byte[] buffer = new byte[inputStream.available()];
    while (inputStream.read(buffer) != -1);

    String jsonText = new String(buffer);
    JSONObject jsonObject = new JSONObject(jsonText);

    JSONObject jsonDropBox = jsonObject.getJSONObject("dropboxpaths");
    JSONArray jsonDropBoxPaths = jsonDropBox.getJSONArray("paths");

    // node map of workflow
    Map<String, String> dropBoxPaths = new HashMap<String, String>();

    // iterate over nodes of workflow
    for (int i = 0; i < jsonDropBoxPaths.length(); i++) {
      JSONObject jsonDropBoxPath = jsonDropBoxPaths.getJSONObject(i);

      dropBoxPaths.put(jsonDropBoxPath.getString("sampletype"),
          jsonDropBoxPath.getString("dropboxpath"));
    }

    inputStream.close();
    return dropBoxPaths;
  }

  public File getCertificate() {
    return this.certificate;
  }
}
