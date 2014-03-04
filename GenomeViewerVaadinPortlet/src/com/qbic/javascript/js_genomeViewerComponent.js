
CELLBASE_HOST = "http://ws-beta.bioinfo.cipf.es/cellbase/rest";
CELLBASE_VERSION = "v3";
var genomeViewer = null;
	
com_qbic_javascript_JSGenomeViewerComponent = function() {

//	var e = this.getElement();
//	console.log(e);
    
	var run = function() {
		$(document.body).append('<div id="gv-app"></div>');
		var region = new Region({chromosome: "13", start: 32889611, end: 32889611});
	var availableSpecies = {
	    "text": "Species",
	    "items": [
	        {
	            "text": "Vertebrates",
	            "items": [
	                {"text": "Homo sapiens", "assembly": "GRCh37.p10", "region": {"chromosome": "13", "start": 32889611, "end": 32889611}, "chromosomes": ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT"], "url": "ftp://ftp.ensembl.org/pub/release-71/"},
	                {"text": "Mus musculus", "assembly": "GRCm38.p1", "region": {"chromosome": "1", "start": 18422009, "end": 18422009}, "chromosomes": ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "X", "Y", "MT"], "url": "ftp://ftp.ensembl.org/pub/release-71/"}
	            ]
	        }
	    ]
	};
	var species = availableSpecies.items[0].items[1];

	genomeViewer = new GenomeViewer({
	    targetId: 'gv-app',
	    region: region,
	    availableSpecies: availableSpecies,
	    species: species,
	    sidePanel: false,
	    autoRender: true,
	    border: true,
	    resizable: true,
	    karyotypePanelConfig: {
	        collapsed: false,
	        collapsible: true
	    },
	    chromosomePanelConfig: {
	        collapsed: false,
	        collapsible: true
	    }

	}); //the div must exist

	genomeViewer.draw();


	var tracks = [];
	    this.sequence = new SequenceTrack({
	        targetId: null,
	        id: 1,
	        title: 'Sequence',
	        histogramZoom: 20,
	        transcriptZoom: 50,
	        height: 30,
	        visibleRange: 200,
	        featureTypes: FEATURE_TYPES,

	        renderer: new SequenceRenderer(),

	        dataAdapter: new SequenceAdapter({
	            category: "genomic",
	            subCategory: "region",
	            resource: "sequence",
	            species: genomeViewer.species,
	            featureCache: {
	                gzip: true,
	                chunkSize: 1000
	            }
	        })
	    });

	    tracks.push(this.sequence);

	    this.gene = new GeneTrack({
	        targetId: null,
	        id: 2,
	        title: 'Gene',
	        minHistogramRegionSize: 20000000,
	        maxLabelRegionSize: 10000000,
	        minTranscriptRegionSize: 200000,
	        height: 140,
	        featureTypes: FEATURE_TYPES,

	        renderer: new GeneRenderer(),

	        dataAdapter: new CellBaseAdapter({
	            category: "genomic",
	            subCategory: "region",
	            resource: "gene",
	            species: genomeViewer.species,
	            cacheConfig: {
	                chunkSize: 50000
	            },
	            filters: {},
	            options: {},
	            featureConfig: FEATURE_CONFIG.gene
	        })
	    });

	    tracks.push(this.gene);


//	    var renderer = new FeatureRenderer('gene');
//	    renderer.on({
//	        'feature:click': function (event) {
//	            console.log(event);
//	            new GeneInfoWidget(null, genomeViewer.species).draw(event);
//	        }
//	    });
//	    var gene = new FeatureTrack({
//	        targetId: null,
//	        id: 2,
//	        title: 'Gene',
//	        minHistogramRegionSize: 20000000,
//	        maxLabelRegionSize: 10000000,
//	        height: 100,
//	        titleVisibility: 'hidden',
//	        featureTypes: FEATURE_TYPES,
//
//	        renderer: renderer,
//
//	        dataAdapter: new CellBaseAdapter({
//	            category: "genomic",
//	            subCategory: "region",
//	            resource: "gene",
//	            params: {
//	                exclude: 'transcripts'
//	            },
//	            species: genomeViewer.species,
//	            cacheConfig: {
//	                chunkSize: 50000
//	            }
//	        })
//	    });
//	    genomeViewer.addOverviewTrack(gene);

	    this.snp = new FeatureTrack({
	        targetId: null,
	        id: 4,
	        title: 'SNP',
	        minHistogramRegionSize: 12000,
	        maxLabelRegionSize: 3000,
	        height: 100,
	        featureTypes: FEATURE_TYPES,

	        renderer: new FeatureRenderer('snp'),

	        dataAdapter: new CellBaseAdapter({
	            category: "genomic",
	            subCategory: "region",
	            resource: "snp",
	            params: {
	                exclude: 'transcriptVariations,xrefs,samples'
	            },
	            species: genomeViewer.species,
	            cacheConfig: {
	                chunkSize: 10000
	            },
	            filters: {},
	            options: {},
	            featureConfig: FEATURE_CONFIG.snp
	        })
	    });

	    tracks.push(this.snp);
	    genomeViewer.addTrack(tracks);
	    
	};
	$(document).ready(run);
	

	this.onStateChange = function() {
		var c = this.getState().chr;
		var s = this.getState().start;
		var e = this.getState().end;
		if(genomeViewer != null && genomeViewer.rendered == true) {
			genomeViewer.setRegion({chromosome:c, start: s, end: e});
		}
	};
	
	var self = this;
	genomeViewer.on("region:change", function() {
		self.func();
	});

};

