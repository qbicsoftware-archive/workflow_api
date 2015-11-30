package fasta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fasta.FastaBean;
import fasta.FastaBean.Type;

public class FastaDB {

  private Map<String, FastaBean> proteomicsSource;
  private Map<String, FastaBean> ngsSource;

  public FastaDB() {
    proteomicsSource = new HashMap<String, FastaBean>();
    ngsSource = new HashMap<String, FastaBean>();
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
                "/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/UCSC/hg19/Sequence/WholeGenomeFasta/genome.fa",
                Type.NGS);
    FastaBean bean5 =
            new FastaBean(
                "Human",
                "Ensembl, GRCh37,  WholeGenomeFasta",
                "GRCh37",
                "Homo_sapiens",
                "/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/Ensembl/GRCh37/Sequence/WholeGenomeFasta/genome.fa",
                Type.NGS);
    FastaBean bean6 =
            new FastaBean(
                "Human",
                "UCSC, hg18,  WholeGenomeFasta",
                "hg18",
                "Homo_sapiens",
                "/lustre_cfc/qbic/reference_genomes/Mus_musculus/DNA/UCSC/hg18/Sequence/WholeGenomeFasta/genome.fa",
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
        new FastaBean(
            "Human",
            "Uniprot Human Proteome",
            "UP000005640",
            "Homo_sapiens",
            "/lustre_cfc/qbic/reference_genomes/Homo_sapiens/Proteome/UP000005640_9606.fasta",
            Type.Proteomics);

    proteomicsSource.put(bean1.getDescription(), bean1);
    proteomicsSource.put(bean3.getDescription(), bean3);
    proteomicsSource.put(bean10.getDescription(), bean10);

    ngsSource.put(bean2.getDescription(), bean2);
    ngsSource.put(bean4.getDescription(), bean4);
    ngsSource.put(bean5.getDescription(), bean5);
    ngsSource.put(bean6.getDescription(), bean6);
    ngsSource.put(bean7.getDescription(), bean7);
    ngsSource.put(bean8.getDescription(), bean8);
    ngsSource.put(bean9.getDescription(), bean9);
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
    return proteomicsSource.size() + ngsSource.size();
  }

  public List<FastaBean> getAll() {
    ArrayList<FastaBean> beans = new ArrayList<FastaBean>(ngsSource.values());
    beans.addAll(proteomicsSource.values());
    return beans;
  }
}
