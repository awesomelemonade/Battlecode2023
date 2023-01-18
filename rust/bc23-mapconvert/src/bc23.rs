use crate::*;

pub struct BC23Map {
    pub name: String,
    pub width: u32,
    pub height: u32,
    pub symmetry: i32,
    pub random_seed: i32,
    pub bodies: Vec<BC23Body>,
    pub walls: Vec<Vec<bool>>,
    pub clouds: Vec<Vec<bool>>,
    pub currents: Vec<Vec<i32>>,
    pub islands: Vec<Vec<i32>>,
    pub resources: Vec<Vec<i32>>,
}

pub struct BC23Body {
    pub id: i32,
    pub team_id: i32,
    pub body_type: schema23::BodyType,
    pub location: (i32, i32),
}

impl BC23Body {
    fn to_body_table<'a>(
        builder: &mut flatbuffers::FlatBufferBuilder<'a>,
        vec: &[BC23Body],
    ) -> flatbuffers::WIPOffset<schema23::SpawnedBodyTable<'a>> {
        let (robot_ids, team_ids, types, locations): (Vec<_>, Vec<_>, Vec<_>, Vec<_>) = vec
            .iter()
            .map(|x| (x.id, x.team_id as i8, x.body_type, x.location))
            .multiunzip();

        let robot_ids = builder.create_vector(robot_ids.as_slice());
        let team_ids = builder.create_vector(team_ids.as_slice());
        let types = builder.create_vector(types.as_slice());

        let (xs, ys): (Vec<_>, Vec<_>) = locations.into_iter().unzip();
        let (xs, ys) = (
            builder.create_vector(xs.as_slice()),
            builder.create_vector(ys.as_slice()),
        );
        let locs = schema23::VecTable::create(
            builder,
            &schema23::VecTableArgs {
                xs: Some(xs),
                ys: Some(ys),
            },
        );

        schema23::SpawnedBodyTable::create(
            builder,
            &schema23::SpawnedBodyTableArgs {
                robotIDs: Some(robot_ids),
                teamIDs: Some(team_ids),
                types: Some(types),
                locs: Some(locs),
            },
        )
    }
}

impl From<BC23Map> for Vec<u8> {
    fn from(bc23_map: BC23Map) -> Self {
        let mut builder = flatbuffers::FlatBufferBuilder::new();

        let name = builder.create_string(&bc23_map.name);
        let min_corner = schema23::Vec::new(0, 0);
        let max_corner = schema23::Vec::new(bc23_map.width as i32, bc23_map.height as i32);

        macro_rules! to_flatbuffers_vec {
            ($expression:expr) => {{
                let copied = $expression.iter().flatten().copied().collect_vec();
                builder.create_vector(copied.as_slice())
            }};
        }

        let bodies = BC23Body::to_body_table(&mut builder, bc23_map.bodies.as_slice());

        let walls = to_flatbuffers_vec!(bc23_map.walls);
        let clouds = to_flatbuffers_vec!(bc23_map.clouds);
        let currents = to_flatbuffers_vec!(bc23_map.currents);
        let islands = to_flatbuffers_vec!(bc23_map.islands);
        let resources = to_flatbuffers_vec!(bc23_map.resources);

        let game_map = schema23::GameMap::create(
            &mut builder,
            &schema23::GameMapArgs {
                name: Some(name),
                minCorner: Some(&min_corner),
                maxCorner: Some(&max_corner),
                symmetry: bc23_map.symmetry,
                bodies: Some(bodies),
                randomSeed: bc23_map.random_seed,
                walls: Some(walls),
                clouds: Some(clouds),
                currents: Some(currents),
                islands: Some(islands),
                resources: Some(resources),
            },
        );
        builder.finish(game_map, None);
        builder.finished_data().to_vec()
    }
}
