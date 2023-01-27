core!();

use itertools::Itertools;
use plotters::prelude::*;
const OUT_FILE_NAME: &'static str = "histogram.png";
pub fn main() -> OrError<()> {
    let random_points = [
        0f64, -1.25, -0.3, 0.35, 0.12, 0.25, 0.71, -0.11, -0.11, -0.11, -0.11,
    ];
    histogram(&random_points, 0.2f64)?;
    Ok(())
}

pub fn histogram(raw_data: &[f64], bucket_size: f64) -> OrError<()> {
    if let itertools::MinMaxResult::MinMax(&min, &max) = raw_data.iter().minmax() {
        // round down
        let min = (min / bucket_size).floor() * bucket_size;
        let max = (max / bucket_size).ceil() * bucket_size;
        let max_count = 10u32;

        let root = BitMapBackend::new(OUT_FILE_NAME, (1024, 768)).into_drawing_area();

        root.fill(&WHITE)?;

        let mut chart = ChartBuilder::on(&root)
            .margin(5)
            .caption("Histogram Plot", ("sans-serif", 30))
            .set_label_area_size(LabelAreaPosition::Left, 60)
            .set_label_area_size(LabelAreaPosition::Bottom, 60)
            .set_label_area_size(LabelAreaPosition::Right, 60)
            .build_cartesian_2d(
                (min..max).step(bucket_size).use_round().into_segmented(),
                0f64..1f64,
            )?
            .set_secondary_coord(
                (min..max).step(bucket_size).use_round().into_segmented(),
                0u32..max_count,
            );

        chart.configure_mesh().draw()?;

        chart.configure_secondary_axes().y_desc("Count").draw()?;

        let actual = Histogram::vertical(chart.borrow_secondary())
            .style(GREEN.filled())
            .margin(3)
            .data(raw_data.iter().map(|x| (*x, 1)));

        chart.draw_secondary_series(actual)?;

        chart.configure_series_labels().draw()?;

        // To avoid the IO failure being ignored silently, we manually call the present function
        root.present().expect("Unable to write result to file");
        println!("Result has been saved to {}", OUT_FILE_NAME);
        Ok(())
    } else {
        Err(Error!("Too few datapoints: {:?}", raw_data))
    }
}
