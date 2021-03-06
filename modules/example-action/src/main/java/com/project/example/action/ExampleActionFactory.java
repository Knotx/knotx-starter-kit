package com.project.example.action;


import static io.knotx.fragments.api.FragmentResult.SUCCESS_TRANSITION;

import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.api.FragmentResult;
import io.knotx.fragments.action.api.Action;
import io.knotx.fragments.action.api.ActionFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ExampleActionFactory implements ActionFactory {

  @Override
  public String getName() {
    return "example-action";
  }

  @Override
  public Action create(String alias, JsonObject config, Vertx vertx, Action doAction) {
    return (fragmentContext, resultHandler) -> {
      Fragment fragment = fragmentContext.getFragment();
      fragment.appendPayload("message", "Hello from example action!");
      fragment.appendPayload("status", "success");
      Future<FragmentResult> resultFuture = Future
          .succeededFuture(new FragmentResult(fragment, SUCCESS_TRANSITION));
      resultFuture.setHandler(resultHandler);
    };
  }

}
