#![feature(let_chains)]

macro_rules! var {
    ($expression:expr) => {
        format!("VAR_{}", $expression)
    };
}

use std::cell::RefCell;
use std::fs;

use itertools::Itertools;

fn main() {
    let buffer = RefCell::new("public static int[] sort() {\n".to_string());
    let funcs_buffer = RefCell::new(String::new());
    gen_sort(
        &buffer,
        &funcs_buffer,
        &[0, 1, 2, 3, 4, 5, 6, 7],
        // &[0, 1, 2, 3],
        Box::new(|merged| {
            let merged = merged.into_iter().map(|x| x.to_string()).join(", ");
            funcs_buffer
                .borrow_mut()
                .push_str(&format!("return new int[] {{{}}};\n", merged));
        }),
    );
    let mut out = buffer.into_inner();
    out.push_str("}\n");
    out.push_str(&funcs_buffer.into_inner());
    fs::write("output.txt", out.as_str()).expect("Unable to write to file");
    // println!("{}", out);
}

fn gen_sort<'a>(
    buffer: &RefCell<String>,
    funcs_buffer: &RefCell<String>,
    elements: &[u32],
    callback: Box<dyn Fn(Vec<u32>) + 'a>,
) {
    if elements.len() == 1 {
        // we're done
        callback(elements.to_vec());
    } else {
        // sort
        let first_half = &elements[..elements.len() / 2];
        let second_half = &elements[elements.len() / 2..];
        gen_sort(
            buffer,
            funcs_buffer,
            first_half,
            Box::new(|first_half_ordered| {
                gen_sort(
                    buffer,
                    funcs_buffer,
                    second_half,
                    Box::new(|second_half_ordered| {
                        // code too large - we need to put in different methods
                        if elements.len() == 8 {
                            let first_merged =
                                first_half_ordered.iter().map(|x| x.to_string()).join("");
                            let second_merged =
                                second_half_ordered.iter().map(|x| x.to_string()).join("");
                            let method_name = format!("sort_{}_{}", first_merged, second_merged);
                            buffer
                                .borrow_mut()
                                .push_str(&format!("return {}();\n", method_name));
                            // we have 2 sorted arrays, let's merge
                            funcs_buffer
                                .borrow_mut()
                                .push_str(&format!("public static int[] {}(){{\n", method_name));
                            gen_merge(
                                funcs_buffer,
                                &first_half_ordered[..],
                                &second_half_ordered[..],
                                Box::new(|merged| {
                                    callback(merged);
                                }),
                            );
                            funcs_buffer.borrow_mut().push_str(&format!("}}"));
                        } else {
                            // we have 2 sorted arrays, let's merge
                            gen_merge(
                                buffer,
                                &first_half_ordered[..],
                                &second_half_ordered[..],
                                Box::new(|merged| {
                                    callback(merged);
                                }),
                            );
                        }
                    }),
                );
            }),
        );
    }
}

fn gen_merge<'a>(
    buffer: &RefCell<String>,
    left: &[u32],
    right: &[u32],
    callback: Box<dyn Fn(Vec<u32>) + 'a>,
) {
    if let Some(&first_left) = left.first() && let Some(&first_right) = right.first() {
        // generate comparison
        buffer.borrow_mut().push_str(&format!("if ({} < {}) {{\n", var!(first_left), var!(first_right)));
        gen_merge(buffer, &left[1..], right, Box::new(|merged| {
            let mut vec = Vec::new();
            vec.push(first_left);
            vec.extend(merged);
            callback(vec);
        }));
        buffer.borrow_mut().push_str(&format!("}} else {{\n"));
        gen_merge(buffer, left, &right[1..], Box::new(|merged| {
            let mut vec = Vec::new();
            vec.push(first_right);
            vec.extend(merged);
            callback(vec);
        }));
        buffer.borrow_mut().push_str(&format!("}}\n"));
    } else {
        if left.is_empty() {
            callback(right.to_vec());
        } else {
            callback(left.to_vec());
        }
    }
}
