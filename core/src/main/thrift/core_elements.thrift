namespace java com.datascience.utils.transformations.thrift.generated

struct TSettings {
	1: string settings
}

struct TKind {
	1: string kind
}

struct TNominalLabel {
	1: string label
}

struct TContLabel {
	1: double value,
	2: double zeta
}

union TLabel {
	1: TNominalLabel nominalLabel,
	2: TContLabel contLabel
}

struct TWorker {
	1: string worker
}

struct TAssign {
	1: TWorker worker,
	2: string object,
	3: TLabel label
}

struct TLObject {
	1: string id,
	2: TLabel goldLabel,
    3: TLabel evalLabel
}

struct TLObjects {
	1: list<TLObject> objects
}

struct TAssigns {
	1: list<TAssign> assigns
}

struct TWorkers {
	1: list<TWorker> workers
}

struct PureNominalData {
	1: string data
}


struct TDatumNominalResults {
	1: map<string, double> categoryProbabilities
}

struct TCMEntry {
	1: string from,
	2: string to,
	3: double val
}

struct TConfusionMatrix { # MultinominalConfusionMatrix
	1: set<string> categories,
	2: list<TCMEntry> matrix,
	3: map<string, double> rowDenominator
}

struct TWorkerNominalResults {
	1: TConfusionMatrix cm,
	2: TConfusionMatrix eval_cm
}


struct TDatumContResults {
	1: double est_val,
	2: double est_zeta,
	3: double distribMu,
	4: double distribSigma
}

struct TWorkerContResults {
	1: double est_rho,
	2: double est_mu,
	3: double est_sigma,
	4: TAssigns zeta,
	5: double true_mu,
	6: double true_sigma,
	7: double true_rho
}
