use itertools::izip;

use crate::*;

pub struct BC22Map {
    pub name: String,
    pub width: u32,
    pub height: u32,
    pub symmetry: i32,
    pub random_seed: i32,
    pub bodies: Vec<BC22Body>,
    pub rubble: Vec<Vec<i32>>,
    pub lead: Vec<Vec<i32>>,
}

pub struct BC22Body {
    pub id: i32,
    pub team_id: i32,
    pub body_type: schema22::BodyType,
    pub location: (i32, i32),
}

impl BC22Body {
    fn try_from(table: &schema22::SpawnedBodyTable<'_>) -> OrError<Vec<BC22Body>> {
        let robot_ids = table
            .robotIDs()
            .ok_or(Error!("Missing robot ids"))?
            .into_iter()
            .collect_vec();
        let team_ids = table
            .teamIDs()
            .ok_or(Error!("Missing team ids"))?
            .into_iter()
            .collect_vec();
        let body_types = table
            .types()
            .ok_or(Error!("Missing body types"))?
            .into_iter()
            .collect_vec();
        let locations = table.locs().ok_or(Error!("Missing locations"))?;
        let xs = locations.xs().ok_or(Error!("Missing xs"))?;
        let ys = locations.ys().ok_or(Error!("Missing xs"))?;
        let locations = xs.iter().zip_eq(ys.iter()).collect_vec();

        let vec = izip!(robot_ids, team_ids, body_types, locations)
            .map(|(robot_id, team_id, body_type, location)| BC22Body {
                id: robot_id,
                team_id: team_id as i32,
                body_type,
                location,
            })
            .collect_vec();
        Ok(vec)
    }
}

impl TryFrom<&[u8]> for BC22Map {
    type Error = Error;
    fn try_from(bytes: &[u8]) -> Result<Self, Self::Error> {
        let game_map = flatbuffers::root::<schema22::GameMap>(bytes)?;
        let name = game_map.name().ok_or(Error!("Missing name"))?;
        let max_corner = game_map.maxCorner().ok_or(Error!("Missing max corner"))?;
        let min_corner = game_map.minCorner().ok_or(Error!("Missing min corner"))?;
        let width: u32 = (max_corner.x() - min_corner.x()).try_into()?;
        let height: u32 = (max_corner.y() - min_corner.y()).try_into()?;

        let parse_array = |data: Option<flatbuffers::Vector<i32>>| {
            let data = data
                .ok_or(Error!("Missing data"))?
                .into_iter()
                .collect_vec();
            let chunked = data
                .chunks(width as usize)
                .map(|x| x.to_vec())
                .collect_vec();
            Ok(chunked)
        };

        let symmetry = game_map.symmetry();
        let random_seed = game_map.randomSeed();
        let bodies = game_map.bodies().ok_or(Error!("Missing bodies"))?;

        let rubble = parse_array(game_map.rubble())?;
        let lead = parse_array(game_map.lead())?;

        // we don't care about anomalies
        // let anomalies = game_map.anomalies();
        // let anomalyRounds = game_map.anomalyRounds();
        Ok(BC22Map {
            name: name.to_string(),
            width,
            height,
            symmetry,
            random_seed,
            bodies: BC22Body::try_from(&bodies)?,
            rubble,
            lead,
        })
    }
}
