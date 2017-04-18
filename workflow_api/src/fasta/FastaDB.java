package fasta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fasta.FastaBean.Type;

public class FastaDB {

  private Map<String, FastaBean> proteomicsSource;
  private Map<String, FastaBean> ngsSource;
  private Map<String, FastaBean> bwaSource;
  private Map<String, FastaBean> barcodesSource;
  private Map<String, FastaBean> shRNAlibrarySource;

  public FastaDB() {
    proteomicsSource = new HashMap<String, FastaBean>();
    ngsSource = new HashMap<String, FastaBean>();
    bwaSource = new HashMap<String, FastaBean>();
    barcodesSource = new HashMap<String, FastaBean>();
    shRNAlibrarySource = new HashMap<String, FastaBean>();

    // TODO more generic way e.g. with gson
    FastaBean bean1 =
        new FastaBean(
            "Guinea pig",
            "Merged Guineapig_NCBI_10feb2015.fasta with Cavia_porcellus.cavPor3.pep.all.ENSEMBL.fa",
            "0.1a",
            "Cavia_porcellus",
            "/lustre_cfc/qbic/reference_genomes/Cavia_porcellus/Proteome/Cavia_porcellus.NCBI_ENSEMBL_nr.fasta",
            Type.Proteomics);
    FastaBean bean2 =
        new FastaBean(
            "Mouse",
            "UCSC_mm10,  WholeGenomeFasta",
            "0.1",
            "Mus_musculus",
            "/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/UCSC_mm10/Sequence/Bowtie2Index:/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/UCSC_mm10/annotation/genes.gtf",
            Type.NGS);
    FastaBean bean3 =
        new FastaBean(
            "Mouse",
            "uniprot mouse taxonomy 10090 keyword 181 20140226",
            "0.1a",
            "Mus_musculus",
            "/lustre_cfc/qbic/reference_genomes/Mus_musculus/Proteome/uniprot-mouse_taxonomy_10090_keyword_181_20140226.fasta",
            Type.Proteomics);
    FastaBean bean4 =
        new FastaBean(
            "Human",
            "UCSC, hg19, WholeGenomeFasta",
            "hg19",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg19/Sequence/WholeGenomeFasta/genome.fa",
            Type.NGS);
    FastaBean bean5 =
        new FastaBean(
            "Human",
            "Ensembl, GRCh37,  WholeGenomeFasta",
            "GRCh37",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/2016.01.21.Ensembl/GRCh37/Sequence/WholeGenomeFasta/genome.fa",
            Type.NGS);
    FastaBean bean6 =
        new FastaBean(
            "Human",
            "UCSC, hg18,  WholeGenomeFasta",
            "hg18",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg18/Sequence/WholeGenomeFasta/genome.fa",
            Type.NGS);
    FastaBean bean7 =
        new FastaBean(
            "Human",
            "UCSC, hg19, BowtieIndex",
            "hg19",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg19/Sequence/Bowtie2Index:/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg19/Annotation/Genes/genes.gtf",
            Type.NGS);
    FastaBean bean8 =
        new FastaBean(
            "Human",
            "Ensembl, GRCh37,  BowtieIndex",
            "GRCh37",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/Ensembl/GRCh37/Sequence/Bowtie2Index:/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/Ensembl/GRCh37/Annotation/Genes/genes.gtf",
            Type.NGS);
    FastaBean bean9 =
        new FastaBean(
            "Human",
            "UCSC, hg18,  BowtieIndex",
            "hg18",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg18/Sequence/Bowtie2Index:/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/UCSC/hg18/Annotation/Genes/genes.gtf",
            Type.NGS);
    FastaBean bean10 =
        new FastaBean("Human", "Uniprot Human Proteome", "UP000005640", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/Proteome/UP000005640_9606.fasta",
            Type.Proteomics);
    FastaBean bean11 =
        new FastaBean(
            "Human",
            "Uniprot Human Proteome Reviewed (SP)",
            "UP000005640",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/Proteome/UP000005640_9606_reviewed.fasta",
            Type.Proteomics);
    FastaBean bean12 =
        new FastaBean(
            "Human",
            "UCSC, hg19, BWAIndex",
            "hg19",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/2016.01.21.UCSC/hg19/Sequence/BWAIndex/hg19/hg19",
            Type.NGS);
    FastaBean bean13 =
        new FastaBean(
            "Mouse",
            "UCSC, mm10, BWAIndex",
            "mm10",
            "Mus_musculus",
            "/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/UCSC_mm10/Sequence/BWAIndex/mm10/mm10",
            Type.NGS);
    FastaBean bean14 =
        new FastaBean("Pool M7", "Library of shRNAs (pool M7)", "1.0", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/pool_M7.tsv", Type.Transcriptomics);
    FastaBean bean15 =
        new FastaBean("4nt barcodes", "List of barcodes for demultiplexing (4nt barcodes)",
            "10/25/2016", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/barcodes/4nt_barcodes.tsv", Type.Transcriptomics);
    FastaBean bean16 =
        new FastaBean("KPP shRNA library", "Library of shRNAs (KPP shRNA)", "1.0", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/KPP_shRNA_list.tsv", Type.Transcriptomics);
    FastaBean bean17 =
        new FastaBean(
            "Schizosaccharomyces Pombe Proteins ",
            "Library of schizosaccharomyces pombe proteins, Nterm corrected, 04/06/2011",
            "1.0",
            "Schizosaccharomyces_pombe",
            "/lustre_cfc/qbic/reference_genomes/Schizosaccharomyces/Proteome/sanger.pompep.mad1.Nterm.corrected_20110406.fasta",
            Type.Proteomics);
    FastaBean bean18 =
        new FastaBean("cRAP protein sequences", "common Repository of Adventitious Proteins, cRAP",
            "30.01.15", "multiple species", "/lustre_cfc/qbic/reference_genomes/crap.fasta",
            Type.Proteomics);
    FastaBean bean19 =
        new FastaBean(
            "NNalpas_PCT_349bact_with_Mmus",
            "Custom database for metaproteomics",
            "20161216",
            "Mus_musculus + bacteria",
            "/lustre_cfc/qbic/reference_genomes/Mus_musculus/Proteome/NNalpas_PCT_349bact_with_Mmus_20161216.fasta",
            Type.Proteomics);
    FastaBean bean20 =
        new FastaBean("3nt barcodes", "List of barcodes for demultiplexing (3nt barcodes)",
            "02/10/2017", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/barcodes/3nt_barcodes.tsv", Type.Transcriptomics);
    FastaBean bean21 =
        new FastaBean("DGG druged library shRNA library", "Library of shRNAs (DGG druged library)",
            "1.0", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/DGG_druged_library.tsv",
            Type.Transcriptomics);
    FastaBean bean22 =
        new FastaBean(
            "Human",
            "Ensembl, GRCh38.78,  BWAIndex",
            "GRCh38.78",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/2014.11.17.Ensembl/GRCH38.78/Sequence/BWAIndex/genome",
            Type.NGS);
    FastaBean bean23 =
        new FastaBean(
            "Human",
            "Ensembl, GRCh38.78,  WholeGenomeFasta",
            "GRCh38.78",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/DNA/2014.11.17.Ensembl/GRCH38.78/Sequence/WholeGenomeFasta/genome.fa",
            Type.NGS);
    FastaBean bean24 =
        new FastaBean("SRM shRNA library", "Library of shRNAs (SRM)", "1.0", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_SRM.tsv",
            Type.Transcriptomics);
    FastaBean bean25 =
        new FastaBean("Schulze shRNA library", "Library of shRNAs (Schulze)", "1.0",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_Schulze.tsv",
            Type.Transcriptomics);
    FastaBean bean26 =
        new FastaBean("Riebold shRNA library", "Library of shRNAs (Riebold)", "1.0",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_Riebold.tsv",
            Type.Transcriptomics);
    FastaBean bean27 =
        new FastaBean("Meierjohann shRNA library", "Library of shRNAs (Meierjohann)", "1.0",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_Meierjohann.tsv",
            Type.Transcriptomics);
    FastaBean bean28 =
        new FastaBean("DNA D+R shRNA library", "Library of shRNAs (DNA D+R)", "1.0",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_DNA_DR.tsv",
            Type.Transcriptomics);
    FastaBean bean29 =
        new FastaBean("Dauch shRNA library", "Library of shRNAs (Dauch)", "1.0", "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/shRNAlibs/shRNA_Library_Dauch.tsv",
            Type.Transcriptomics);

    proteomicsSource.put(bean1.getDescription(), bean1);
    proteomicsSource.put(bean3.getDescription(), bean3);
    proteomicsSource.put(bean10.getDescription(), bean10);
    proteomicsSource.put(bean11.getDescription(), bean11);
    proteomicsSource.put(bean17.getDescription(), bean17);
    proteomicsSource.put(bean18.getDescription(), bean18);
    proteomicsSource.put(bean19.getDescription(), bean19);

    ngsSource.put(bean2.getDescription(), bean2);
    ngsSource.put(bean4.getDescription(), bean4);
    ngsSource.put(bean5.getDescription(), bean5);
    ngsSource.put(bean6.getDescription(), bean6);
    ngsSource.put(bean7.getDescription(), bean7);
    ngsSource.put(bean8.getDescription(), bean8);
    ngsSource.put(bean9.getDescription(), bean9);
    ngsSource.put(bean23.getDescription(), bean23);

    // bwa indices
    bwaSource.put(bean12.getDescription(), bean12);
    bwaSource.put(bean13.getDescription(), bean13);
    bwaSource.put(bean22.getDescription(), bean22);

    // transcriptomic static files and databases
    shRNAlibrarySource.put(bean14.getDescription(), bean14);
    shRNAlibrarySource.put(bean16.getDescription(), bean16);
    shRNAlibrarySource.put(bean21.getDescription(), bean21);
    shRNAlibrarySource.put(bean21.getDescription(), bean24);
    shRNAlibrarySource.put(bean21.getDescription(), bean25);
    shRNAlibrarySource.put(bean21.getDescription(), bean26);
    shRNAlibrarySource.put(bean21.getDescription(), bean27);
    shRNAlibrarySource.put(bean21.getDescription(), bean28);
    shRNAlibrarySource.put(bean21.getDescription(), bean29);

    barcodesSource.put(bean15.getDescription(), bean15);
    barcodesSource.put(bean20.getDescription(), bean20);
  }

  public List<FastaBean> get(int start, int end) {
    int dbSize = this.size();
    if (start > dbSize || start >= end) {
      return new ArrayList<FastaBean>();
    }

    if (start < 0) {
      start = 0;
    }
    if (end > dbSize) {
      end = dbSize;
    }
    List<FastaBean> ret = null;

    if (start < ngsSource.size() && end >= ngsSource.size()) {
      List<FastaBean> ngs = getNgsBeans(start, ngsSource.size());
      List<FastaBean> proteomics = getProteomicBeans(0, end - ngsSource.size());
      ret = new ArrayList<FastaBean>(ngs);
      ret.addAll(proteomics);
    } else if (start < ngsSource.size() && end <= ngsSource.size()) {
      ret = getNgsBeans(start, end);
    } else if (start >= ngsSource.size()) {
      ret = getProteomicBeans(start - ngsSource.size(), end - ngsSource.size());
    }
    return ret;

  }

  private List<FastaBean> getProteomicBeans(int start, int end) {
    if (start > this.proteomicsSource.size() || start >= end) {
      return new ArrayList<FastaBean>();
    }

    if (start < 0) {
      start = 0;
    }
    if (end > this.proteomicsSource.size()) {
      end = this.proteomicsSource.size();
    }
    FastaBean[] bean = proteomicsSource.values().toArray(new FastaBean[0]);
    ArrayList<FastaBean> list = new ArrayList<FastaBean>();
    for (int i = start; i < end; ++i) {
      list.add(bean[i]);
    }
    return list;
  }

  private List<FastaBean> getNgsBeans(int start, int end) {
    if (start > this.ngsSource.size() || start >= end) {
      return new ArrayList<FastaBean>();
    }

    if (start < 0) {
      start = 0;
    }
    if (end > this.ngsSource.size()) {
      end = this.ngsSource.size();
    }
    FastaBean[] bean = ngsSource.values().toArray(new FastaBean[0]);
    ArrayList<FastaBean> list = new ArrayList<FastaBean>();
    for (int i = start; i < end; ++i) {
      list.add(bean[i]);
    }
    return list;
  }

  public int size() {
    return proteomicsSource.size() + ngsSource.size() + bwaSource.size();
  }

  public List<FastaBean> getAll() {
    ArrayList<FastaBean> beans = new ArrayList<FastaBean>(ngsSource.values());
    beans.addAll(proteomicsSource.values());
    beans.addAll(bwaSource.values());
    beans.addAll(shRNAlibrarySource.values());
    beans.addAll(barcodesSource.values());
    return beans;
  }

  public List<FastaBean> getBWAIndices() {
    ArrayList<FastaBean> beans = new ArrayList<FastaBean>(bwaSource.values());
    return beans;
  }

  public List<FastaBean> getBarcodeBeans() {
    ArrayList<FastaBean> beans = new ArrayList<FastaBean>(barcodesSource.values());
    return beans;
  }

  public List<FastaBean> getshRNABeans() {
    ArrayList<FastaBean> beans = new ArrayList<FastaBean>(shRNAlibrarySource.values());
    return beans;
  }
}
