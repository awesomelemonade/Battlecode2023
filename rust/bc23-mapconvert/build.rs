use std::path::Path;

fn main() {
    println!("cargo:rerun-if-changed=battlecode22.fbs");
    println!("cargo:rerun-if-changed=battlecode23.fbs");
    flatc_rust::run(flatc_rust::Args {
        inputs: &[Path::new("battlecode22.fbs")],
        out_dir: Path::new("target/flatbuffers/"),
        ..Default::default()
    })
    .expect("flatc");
    flatc_rust::run(flatc_rust::Args {
        inputs: &[Path::new("battlecode23.fbs")],
        out_dir: Path::new("target/flatbuffers/"),
        ..Default::default()
    })
    .expect("flatc");
}
