namespace java com.datascience.utils.transformations.thrift.generated

struct Settings {
	1: string settings
}

struct Kind {
	1: string kind
}

struct NominalLabel {
	1: string label
}

struct ContLabel {
	1: double value,
	2: double zeta
}

union Label {
	1: NominalLabel nominalLabel,
	2: ContLabel contLabel
}

struct Worker {
	1: string worker
}

struct Assign {
	1: Worker worker,
	2: string object,
	3: Label label
}

struct LObject {
	1: string id,
	2: Label goldLabel,
    3: Label evalLabel
}

struct LObjects {
	1: list<LObject> objects
}

struct Assigns {
	1: list<Assign> assigns
}

struct Workers {
	1: list<Worker> workers
}

# struct PureNominalData { }


struct DatumNominalResults {
	1: map<string, double> categoryProbabilities
}

struct CMEntry {
	1: string from,
	2: string to,
	3: double val
}

struct ConfusionMatrix { # MultinominalConfusionMatrix
	1: set<string> categories,
	2: list<CMEntry> matrix,
	3: map<string, double> rowDenominator
}

struct WorkerNominalResults {
	1: ConfusionMatrix cm,
	2: ConfusionMatrix eval_cm
}


struct DatumContResults {
	1: double est_val,
	2: double est_zeta,
	3: double distribMu,
	4: double distribSigma
}

struct WorkerContResults {
	1: double est_rho,
	2: double est_mu,
	3: double est_sigma,
	4: Assigns zeta,
	5: double true_mu,
	6: double true_sigma,
	7: double true_rho
}
